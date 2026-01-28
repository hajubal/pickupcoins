import { Test, TestingModule } from '@nestjs/testing';
import { JwtService } from '@nestjs/jwt';
import { ConfigService } from '@nestjs/config';
import { UnauthorizedException } from '@nestjs/common';
import * as bcrypt from 'bcrypt';
import { AuthService, JwtPayload } from './auth.service';
import { PrismaService } from '../prisma/prisma.service';

jest.mock('bcrypt');

describe('AuthService', () => {
  let service: AuthService;
  let prismaService: jest.Mocked<PrismaService>;
  let jwtService: jest.Mocked<JwtService>;
  let configService: jest.Mocked<ConfigService>;

  const mockUser = {
    id: BigInt(1),
    loginId: 'testuser',
    userName: 'Test User',
    password: 'hashedPassword',
    active: true,
    slackWebhookUrl: null,
    createdDate: new Date(),
    modifiedDate: new Date(),
  };

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [
        AuthService,
        {
          provide: PrismaService,
          useValue: {
            siteUser: {
              findUnique: jest.fn(),
            },
          },
        },
        {
          provide: JwtService,
          useValue: {
            sign: jest.fn(),
            verify: jest.fn(),
          },
        },
        {
          provide: ConfigService,
          useValue: {
            get: jest.fn(),
          },
        },
      ],
    }).compile();

    service = module.get<AuthService>(AuthService);
    prismaService = module.get(PrismaService);
    jwtService = module.get(JwtService);
    configService = module.get(ConfigService);
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  describe('validateUser', () => {
    it('should return user when credentials are valid', async () => {
      prismaService.siteUser.findUnique = jest.fn().mockResolvedValue(mockUser);
      (bcrypt.compare as jest.Mock).mockResolvedValue(true);

      const result = await service.validateUser('testuser', 'password');

      expect(result).toEqual(mockUser);
      expect(prismaService.siteUser.findUnique).toHaveBeenCalledWith({
        where: { loginId: 'testuser' },
      });
    });

    it('should return null when user is not found', async () => {
      prismaService.siteUser.findUnique = jest.fn().mockResolvedValue(null);

      const result = await service.validateUser('nonexistent', 'password');

      expect(result).toBeNull();
    });

    it('should return null when user is inactive', async () => {
      prismaService.siteUser.findUnique = jest.fn().mockResolvedValue({
        ...mockUser,
        active: false,
      });

      const result = await service.validateUser('testuser', 'password');

      expect(result).toBeNull();
    });

    it('should return null when password is invalid', async () => {
      prismaService.siteUser.findUnique = jest.fn().mockResolvedValue(mockUser);
      (bcrypt.compare as jest.Mock).mockResolvedValue(false);

      const result = await service.validateUser('testuser', 'wrongpassword');

      expect(result).toBeNull();
    });
  });

  describe('login', () => {
    it('should return tokens on successful login', async () => {
      prismaService.siteUser.findUnique = jest.fn().mockResolvedValue(mockUser);
      (bcrypt.compare as jest.Mock).mockResolvedValue(true);
      jwtService.sign = jest.fn().mockReturnValueOnce('access_token').mockReturnValueOnce('refresh_token');
      configService.get = jest.fn().mockReturnValue(900000);

      const result = await service.login({
        loginId: 'testuser',
        password: 'password',
      });

      expect(result).toEqual({
        accessToken: 'access_token',
        refreshToken: 'refresh_token',
        loginId: 'testuser',
        userName: 'Test User',
      });
    });

    it('should throw UnauthorizedException on invalid credentials', async () => {
      prismaService.siteUser.findUnique = jest.fn().mockResolvedValue(null);

      await expect(service.login({ loginId: 'testuser', password: 'wrong' })).rejects.toThrow(UnauthorizedException);
    });

    it('should create refresh token with extended validity when rememberMe is true', async () => {
      prismaService.siteUser.findUnique = jest.fn().mockResolvedValue(mockUser);
      (bcrypt.compare as jest.Mock).mockResolvedValue(true);
      jwtService.sign = jest.fn().mockReturnValue('token');
      configService.get = jest.fn().mockImplementation((key: string) => {
        if (key === 'jwt.rememberMeTokenValidity') return 1296000000; // 15 days
        if (key === 'jwt.accessTokenValidity') return 900000;
        return 604800000; // 7 days
      });

      await service.login({
        loginId: 'testuser',
        password: 'password',
        rememberMe: true,
      });

      expect(jwtService.sign).toHaveBeenCalledTimes(2);
    });
  });

  describe('refreshTokens', () => {
    it('should return new tokens on valid refresh token', async () => {
      const mockPayload: JwtPayload = {
        sub: '1',
        loginId: 'testuser',
        userName: 'Test User',
        type: 'refresh',
      };

      jwtService.verify = jest.fn().mockReturnValue(mockPayload);
      prismaService.siteUser.findUnique = jest.fn().mockResolvedValue(mockUser);
      jwtService.sign = jest.fn().mockReturnValueOnce('new_access_token').mockReturnValueOnce('new_refresh_token');
      configService.get = jest.fn().mockReturnValue(900000);

      const result = await service.refreshTokens('valid_refresh_token');

      expect(result).toEqual({
        accessToken: 'new_access_token',
        refreshToken: 'new_refresh_token',
      });
    });

    it('should throw UnauthorizedException when token type is not refresh', async () => {
      const mockPayload: JwtPayload = {
        sub: '1',
        loginId: 'testuser',
        userName: 'Test User',
        type: 'access',
      };

      jwtService.verify = jest.fn().mockReturnValue(mockPayload);

      await expect(service.refreshTokens('access_token')).rejects.toThrow(UnauthorizedException);
    });

    it('should throw UnauthorizedException when user is not found', async () => {
      const mockPayload: JwtPayload = {
        sub: '999',
        loginId: 'testuser',
        userName: 'Test User',
        type: 'refresh',
      };

      jwtService.verify = jest.fn().mockReturnValue(mockPayload);
      prismaService.siteUser.findUnique = jest.fn().mockResolvedValue(null);

      await expect(service.refreshTokens('valid_refresh_token')).rejects.toThrow(UnauthorizedException);
    });

    it('should throw UnauthorizedException when user is inactive', async () => {
      const mockPayload: JwtPayload = {
        sub: '1',
        loginId: 'testuser',
        userName: 'Test User',
        type: 'refresh',
      };

      jwtService.verify = jest.fn().mockReturnValue(mockPayload);
      prismaService.siteUser.findUnique = jest.fn().mockResolvedValue({
        ...mockUser,
        active: false,
      });

      await expect(service.refreshTokens('valid_refresh_token')).rejects.toThrow(UnauthorizedException);
    });

    it('should throw UnauthorizedException on invalid token', async () => {
      jwtService.verify = jest.fn().mockImplementation(() => {
        throw new Error('Invalid token');
      });

      await expect(service.refreshTokens('invalid_token')).rejects.toThrow(UnauthorizedException);
    });
  });

  describe('validateJwtPayload', () => {
    it('should return user info on valid payload', async () => {
      const mockPayload: JwtPayload = {
        sub: '1',
        loginId: 'testuser',
        userName: 'Test User',
        type: 'access',
      };

      prismaService.siteUser.findUnique = jest.fn().mockResolvedValue(mockUser);

      const result = await service.validateJwtPayload(mockPayload);

      expect(result).toEqual({
        userId: mockUser.id,
        loginId: mockUser.loginId,
        userName: mockUser.userName,
      });
    });

    it('should throw UnauthorizedException when user is not found', async () => {
      const mockPayload: JwtPayload = {
        sub: '999',
        loginId: 'testuser',
        userName: 'Test User',
        type: 'access',
      };

      prismaService.siteUser.findUnique = jest.fn().mockResolvedValue(null);

      await expect(service.validateJwtPayload(mockPayload)).rejects.toThrow(UnauthorizedException);
    });
  });
});

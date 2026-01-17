import { Test, TestingModule } from '@nestjs/testing';
import { NotFoundException } from '@nestjs/common';
import { CookieService } from './cookie.service';
import { PrismaService } from '../../prisma/prisma.service';
import { Cookie } from '@prisma/client';

describe('CookieService', () => {
  let service: CookieService;
  let prismaService: jest.Mocked<PrismaService>;

  const mockCookie: Cookie = {
    id: BigInt(1),
    userName: 'testuser',
    siteName: 'naver',
    cookie: 'NID=123;JSESSIONID=abc',
    isValid: true,
    siteUserId: BigInt(1),
    createdDate: new Date(),
    modifiedDate: new Date(),
    createdBy: null,
    lastModifiedBy: null,
  };

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [
        CookieService,
        {
          provide: PrismaService,
          useValue: {
            cookie: {
              findMany: jest.fn(),
              findUnique: jest.fn(),
              create: jest.fn(),
              update: jest.fn(),
              delete: jest.fn(),
            },
          },
        },
      ],
    }).compile();

    service = module.get<CookieService>(CookieService);
    prismaService = module.get(PrismaService);
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  describe('findAll', () => {
    it('should return all cookies', async () => {
      const mockCookies = [mockCookie, { ...mockCookie, id: BigInt(2) }];
      prismaService.cookie.findMany = jest.fn().mockResolvedValue(mockCookies);

      const result = await service.findAll();

      expect(result).toHaveLength(2);
      expect(prismaService.cookie.findMany).toHaveBeenCalledWith({
        orderBy: { createdDate: 'desc' },
      });
    });

    it('should return empty array when no cookies exist', async () => {
      prismaService.cookie.findMany = jest.fn().mockResolvedValue([]);

      const result = await service.findAll();

      expect(result).toEqual([]);
    });
  });

  describe('findOne', () => {
    it('should return a cookie by id', async () => {
      prismaService.cookie.findUnique = jest.fn().mockResolvedValue(mockCookie);

      const result = await service.findOne(BigInt(1));

      expect(result.id).toBe(mockCookie.id.toString());
      expect(result.userName).toBe(mockCookie.userName);
    });

    it('should throw NotFoundException when cookie not found', async () => {
      prismaService.cookie.findUnique = jest.fn().mockResolvedValue(null);

      await expect(service.findOne(BigInt(999))).rejects.toThrow(NotFoundException);
    });
  });

  describe('create', () => {
    it('should create a new cookie', async () => {
      prismaService.cookie.create = jest.fn().mockResolvedValue(mockCookie);

      const result = await service.create(BigInt(1), {
        userName: 'testuser',
        siteName: 'naver',
        cookie: 'NID=123;JSESSIONID=abc',
      });

      expect(result.userName).toBe('testuser');
      expect(prismaService.cookie.create).toHaveBeenCalledWith({
        data: {
          userName: 'testuser',
          siteName: 'naver',
          cookie: 'NID=123;JSESSIONID=abc',
          isValid: true,
          siteUserId: BigInt(1),
        },
      });
    });

    it('should create cookie with isValid set to false when specified', async () => {
      const inactiveCookie = { ...mockCookie, isValid: false };
      prismaService.cookie.create = jest.fn().mockResolvedValue(inactiveCookie);

      await service.create(BigInt(1), {
        userName: 'testuser',
        siteName: 'naver',
        cookie: 'NID=123;JSESSIONID=abc',
        isValid: false,
      });

      expect(prismaService.cookie.create).toHaveBeenCalledWith({
        data: expect.objectContaining({
          isValid: false,
        }),
      });
    });
  });

  describe('update', () => {
    it('should update an existing cookie', async () => {
      prismaService.cookie.findUnique = jest.fn().mockResolvedValue(mockCookie);
      const updatedCookie = { ...mockCookie, userName: 'updateduser' };
      prismaService.cookie.update = jest.fn().mockResolvedValue(updatedCookie);

      const result = await service.update(BigInt(1), { userName: 'updateduser' });

      expect(result.userName).toBe('updateduser');
    });

    it('should throw NotFoundException when cookie not found', async () => {
      prismaService.cookie.findUnique = jest.fn().mockResolvedValue(null);

      await expect(
        service.update(BigInt(999), { userName: 'updateduser' }),
      ).rejects.toThrow(NotFoundException);
    });

    it('should preserve existing values when not provided', async () => {
      prismaService.cookie.findUnique = jest.fn().mockResolvedValue(mockCookie);
      prismaService.cookie.update = jest.fn().mockResolvedValue(mockCookie);

      await service.update(BigInt(1), { userName: 'newuser' });

      expect(prismaService.cookie.update).toHaveBeenCalledWith({
        where: { id: BigInt(1) },
        data: {
          userName: 'newuser',
          siteName: mockCookie.siteName,
          cookie: mockCookie.cookie,
          isValid: mockCookie.isValid,
        },
      });
    });
  });

  describe('delete', () => {
    it('should delete a cookie', async () => {
      prismaService.cookie.findUnique = jest.fn().mockResolvedValue(mockCookie);
      prismaService.cookie.delete = jest.fn().mockResolvedValue(mockCookie);

      await service.delete(BigInt(1));

      expect(prismaService.cookie.delete).toHaveBeenCalledWith({
        where: { id: BigInt(1) },
      });
    });

    it('should throw NotFoundException when cookie not found', async () => {
      prismaService.cookie.findUnique = jest.fn().mockResolvedValue(null);

      await expect(service.delete(BigInt(999))).rejects.toThrow(NotFoundException);
    });
  });

  describe('toggleValidity', () => {
    it('should toggle validity from true to false', async () => {
      prismaService.cookie.findUnique = jest.fn().mockResolvedValue(mockCookie);
      const toggledCookie = { ...mockCookie, isValid: false };
      prismaService.cookie.update = jest.fn().mockResolvedValue(toggledCookie);

      const result = await service.toggleValidity(BigInt(1));

      expect(result.isValid).toBe(false);
      expect(prismaService.cookie.update).toHaveBeenCalledWith({
        where: { id: BigInt(1) },
        data: { isValid: false },
      });
    });

    it('should toggle validity from false to true', async () => {
      const invalidCookie = { ...mockCookie, isValid: false };
      prismaService.cookie.findUnique = jest.fn().mockResolvedValue(invalidCookie);
      const toggledCookie = { ...mockCookie, isValid: true };
      prismaService.cookie.update = jest.fn().mockResolvedValue(toggledCookie);

      const result = await service.toggleValidity(BigInt(1));

      expect(result.isValid).toBe(true);
    });

    it('should throw NotFoundException when cookie not found', async () => {
      prismaService.cookie.findUnique = jest.fn().mockResolvedValue(null);

      await expect(service.toggleValidity(BigInt(999))).rejects.toThrow(
        NotFoundException,
      );
    });
  });

  describe('findValidCookies', () => {
    it('should return only valid cookies', async () => {
      const validCookies = [mockCookie];
      prismaService.cookie.findMany = jest.fn().mockResolvedValue(validCookies);

      const result = await service.findValidCookies();

      expect(result).toHaveLength(1);
      expect(prismaService.cookie.findMany).toHaveBeenCalledWith({
        where: { isValid: true },
        orderBy: { createdDate: 'desc' },
      });
    });

    it('should return empty array when no valid cookies', async () => {
      prismaService.cookie.findMany = jest.fn().mockResolvedValue([]);

      const result = await service.findValidCookies();

      expect(result).toEqual([]);
    });
  });
});

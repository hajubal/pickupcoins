import { Injectable, UnauthorizedException, Logger } from '@nestjs/common';
import { JwtService } from '@nestjs/jwt';
import { ConfigService } from '@nestjs/config';
import * as bcrypt from 'bcrypt';
import { PrismaService } from '../prisma/prisma.service';
import { LoginRequestDto, LoginResponseDto, RefreshTokenResponseDto } from './dto/login.dto';

export interface JwtPayload {
  sub: bigint;
  loginId: string;
  userName: string;
  type: 'access' | 'refresh';
}

@Injectable()
export class AuthService {
  private readonly logger = new Logger(AuthService.name);

  constructor(
    private readonly prisma: PrismaService,
    private readonly jwtService: JwtService,
    private readonly configService: ConfigService,
  ) {}

  async validateUser(loginId: string, password: string) {
    const user = await this.prisma.siteUser.findUnique({
      where: { loginId },
    });

    if (!user || !user.active) {
      return null;
    }

    const isPasswordValid = await bcrypt.compare(password, user.password);
    if (!isPasswordValid) {
      return null;
    }

    return user;
  }

  async login(loginDto: LoginRequestDto): Promise<LoginResponseDto> {
    this.logger.log(`Login attempt for user: ${loginDto.loginId}`);

    const user = await this.validateUser(loginDto.loginId, loginDto.password);
    if (!user) {
      this.logger.warn(`Login failed for user: ${loginDto.loginId}`);
      throw new UnauthorizedException('Invalid credentials');
    }

    const accessToken = this.createAccessToken(user.id, user.loginId, user.userName);
    const refreshToken = this.createRefreshToken(user.id, user.loginId, user.userName, loginDto.rememberMe);

    this.logger.log(`Login successful for user: ${loginDto.loginId}`);

    return {
      accessToken,
      refreshToken,
      loginId: user.loginId,
      userName: user.userName,
    };
  }

  async refreshTokens(refreshToken: string): Promise<RefreshTokenResponseDto> {
    try {
      const payload = this.jwtService.verify<JwtPayload>(refreshToken);

      if (payload.type !== 'refresh') {
        throw new UnauthorizedException('Invalid token type');
      }

      const user = await this.prisma.siteUser.findUnique({
        where: { id: BigInt(payload.sub) },
      });

      if (!user || !user.active) {
        throw new UnauthorizedException('User not found or inactive');
      }

      const newAccessToken = this.createAccessToken(user.id, user.loginId, user.userName);
      const newRefreshToken = this.createRefreshToken(user.id, user.loginId, user.userName, false);

      return {
        accessToken: newAccessToken,
        refreshToken: newRefreshToken,
      };
    } catch {
      throw new UnauthorizedException('Invalid refresh token');
    }
  }

  private createAccessToken(userId: bigint, loginId: string, userName: string): string {
    const payload: JwtPayload = {
      sub: userId,
      loginId,
      userName,
      type: 'access',
    };

    const validity = this.configService.get<number>('jwt.accessTokenValidity') || 900000;

    return this.jwtService.sign(payload, {
      expiresIn: Math.floor(validity / 1000), // Convert to seconds
    });
  }

  private createRefreshToken(userId: bigint, loginId: string, userName: string, rememberMe?: boolean): string {
    const payload: JwtPayload = {
      sub: userId,
      loginId,
      userName,
      type: 'refresh',
    };

    const validity = rememberMe
      ? this.configService.get<number>('jwt.rememberMeTokenValidity') || 1296000000
      : this.configService.get<number>('jwt.refreshTokenValidity') || 604800000;

    return this.jwtService.sign(payload, {
      expiresIn: Math.floor(validity / 1000), // Convert to seconds
    });
  }

  async validateJwtPayload(payload: JwtPayload) {
    const user = await this.prisma.siteUser.findUnique({
      where: { id: BigInt(payload.sub) },
    });

    if (!user || !user.active) {
      throw new UnauthorizedException('User not found or inactive');
    }

    return {
      userId: user.id,
      loginId: user.loginId,
      userName: user.userName,
    };
  }
}

import { Injectable, UnauthorizedException, Logger } from '@nestjs/common';
import { JwtService } from '@nestjs/jwt';
import { ConfigService } from '@nestjs/config';
import * as bcrypt from 'bcrypt';
import { PrismaService } from '../prisma/prisma.service';
import { LoginRequestDto, LoginResponseDto, RefreshTokenResponseDto } from './dto/login.dto';

/**
 * JWT 토큰 페이로드 인터페이스
 * - sub: 사용자 고유 ID (BigInt)
 * - loginId: 로그인 아이디
 * - userName: 사용자 이름
 * - type: 토큰 유형 (access: 액세스 토큰, refresh: 리프레시 토큰)
 */
export interface JwtPayload {
  sub: string; // BigInt를 문자열로 직렬화
  loginId: string;
  userName: string;
  type: 'access' | 'refresh';
}

/**
 * 인증 서비스
 *
 * JWT 기반 인증을 처리하는 핵심 서비스
 * - 사용자 로그인 검증
 * - Access Token / Refresh Token 발급
 * - 토큰 갱신 (Remember Me 지원)
 */
@Injectable()
export class AuthService {
  private readonly logger = new Logger(AuthService.name);

  constructor(
    private readonly prisma: PrismaService,
    private readonly jwtService: JwtService,
    private readonly configService: ConfigService,
  ) {}

  /**
   * 사용자 자격 증명 검증
   *
   * @param loginId - 로그인 아이디
   * @param password - 평문 비밀번호
   * @returns 검증 성공 시 사용자 정보, 실패 시 null
   *
   * 검증 실패 조건:
   * 1. 사용자가 존재하지 않음
   * 2. 사용자가 비활성화 상태 (active = false)
   * 3. 비밀번호 불일치 (bcrypt 해시 비교)
   */
  async validateUser(loginId: string, password: string) {
    // DB에서 loginId로 사용자 조회
    const user = await this.prisma.siteUser.findUnique({
      where: { loginId },
    });

    // 사용자가 없거나 비활성화된 경우 null 반환
    if (!user || !user.active) {
      return null;
    }

    // bcrypt로 비밀번호 해시 비교
    const isPasswordValid = await bcrypt.compare(password, user.password);
    if (!isPasswordValid) {
      return null;
    }

    return user;
  }

  /**
   * 로그인 처리
   *
   * @param loginDto - 로그인 요청 DTO (loginId, password, rememberMe)
   * @returns 액세스 토큰, 리프레시 토큰, 사용자 정보
   * @throws UnauthorizedException - 자격 증명 실패 시
   *
   * 처리 흐름:
   * 1. validateUser()로 자격 증명 검증
   * 2. 검증 성공 시 Access Token 생성 (15분 유효)
   * 3. Refresh Token 생성 (7일 또는 Remember Me 시 15일)
   * 4. 토큰과 사용자 정보 반환
   */
  async login(loginDto: LoginRequestDto): Promise<LoginResponseDto> {
    this.logger.log(`Login attempt for user: ${loginDto.loginId}`);

    // 사용자 자격 증명 검증
    const user = await this.validateUser(loginDto.loginId, loginDto.password);
    if (!user) {
      this.logger.warn(`Login failed for user: ${loginDto.loginId}`);
      throw new UnauthorizedException('Invalid credentials');
    }

    // JWT 토큰 생성
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

  /**
   * 토큰 갱신
   *
   * @param refreshToken - 기존 리프레시 토큰
   * @returns 새로운 액세스 토큰과 리프레시 토큰
   * @throws UnauthorizedException - 토큰 검증 실패 시
   *
   * 처리 흐름:
   * 1. 리프레시 토큰 검증 (서명 확인)
   * 2. 토큰 타입이 'refresh'인지 확인
   * 3. 토큰에서 추출한 사용자 ID로 DB 조회
   * 4. 사용자가 활성 상태인지 확인
   * 5. 새로운 토큰 쌍 발급
   */
  async refreshTokens(refreshToken: string): Promise<RefreshTokenResponseDto> {
    try {
      // 리프레시 토큰 검증 및 페이로드 추출
      const payload = this.jwtService.verify<JwtPayload>(refreshToken);

      // 토큰 타입 확인 (반드시 refresh 토큰이어야 함)
      if (payload.type !== 'refresh') {
        throw new UnauthorizedException('Invalid token type');
      }

      // DB에서 사용자 조회 (토큰의 sub 값 = 사용자 ID)
      const user = await this.prisma.siteUser.findUnique({
        where: { id: BigInt(payload.sub) },
      });

      // 사용자가 없거나 비활성화된 경우 거부
      if (!user || !user.active) {
        throw new UnauthorizedException('User not found or inactive');
      }

      // 새로운 토큰 쌍 생성
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

  /**
   * Access Token 생성
   *
   * @param userId - 사용자 ID
   * @param loginId - 로그인 아이디
   * @param userName - 사용자 이름
   * @returns JWT 액세스 토큰 문자열
   *
   * 유효 기간: 15분 (900,000ms)
   * 용도: API 요청 인증
   */
  private createAccessToken(userId: bigint, loginId: string, userName: string): string {
    const payload: JwtPayload = {
      sub: userId.toString(), // BigInt를 문자열로 변환
      loginId,
      userName,
      type: 'access',
    };

    // 설정에서 유효 기간 조회 (기본값: 15분)
    const validity = this.configService.get<number>('jwt.accessTokenValidity') || 900000;

    return this.jwtService.sign(payload, {
      expiresIn: Math.floor(validity / 1000), // ms를 초로 변환
    });
  }

  /**
   * Refresh Token 생성
   *
   * @param userId - 사용자 ID
   * @param loginId - 로그인 아이디
   * @param userName - 사용자 이름
   * @param rememberMe - Remember Me 옵션 활성화 여부
   * @returns JWT 리프레시 토큰 문자열
   *
   * 유효 기간:
   * - Remember Me 활성화: 15일 (1,296,000,000ms)
   * - Remember Me 비활성화: 7일 (604,800,000ms)
   * 용도: Access Token 갱신
   */
  private createRefreshToken(userId: bigint, loginId: string, userName: string, rememberMe?: boolean): string {
    const payload: JwtPayload = {
      sub: userId.toString(), // BigInt를 문자열로 변환
      loginId,
      userName,
      type: 'refresh',
    };

    // Remember Me 여부에 따라 유효 기간 결정
    const validity = rememberMe
      ? this.configService.get<number>('jwt.rememberMeTokenValidity') || 1296000000  // 15일
      : this.configService.get<number>('jwt.refreshTokenValidity') || 604800000;     // 7일

    return this.jwtService.sign(payload, {
      expiresIn: Math.floor(validity / 1000), // ms를 초로 변환
    });
  }

  /**
   * JWT 페이로드 검증 (Passport Strategy에서 사용)
   *
   * @param payload - JWT 페이로드
   * @returns 검증된 사용자 정보
   * @throws UnauthorizedException - 사용자가 없거나 비활성화 상태일 때
   *
   * 용도: JwtStrategy에서 매 요청마다 호출하여 토큰의 사용자가 유효한지 확인
   */
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

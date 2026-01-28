import { Injectable, NotFoundException, Logger } from '@nestjs/common';
import { PrismaService } from '../../prisma/prisma.service';
import { CreateCookieDto, UpdateCookieDto, CookieResponseDto } from './dto/cookie.dto';

/**
 * 쿠키 관리 서비스
 *
 * 네이버 세션 쿠키를 관리하는 CRUD 서비스
 *
 * 쿠키의 역할:
 * - 네이버 포인트 URL 호출 시 인증에 사용
 * - 각 사용자별로 여러 쿠키 등록 가능 (다중 계정)
 * - 유효성 상태로 활성/비활성 관리
 *
 * 데이터 모델:
 * - Cookie: 쿠키 정보 (사용자명, 사이트명, 쿠키 문자열, 유효성)
 * - SiteUser와 1:N 관계 (한 관리자가 여러 쿠키 보유)
 */
@Injectable()
export class CookieService {
  private readonly logger = new Logger(CookieService.name);

  constructor(private readonly prisma: PrismaService) {}

  /**
   * 모든 쿠키 목록 조회
   *
   * @returns 쿠키 목록 (최신순 정렬)
   *
   * 용도: 관리자 화면에서 쿠키 목록 표시
   */
  async findAll(): Promise<CookieResponseDto[]> {
    this.logger.log('Getting all cookies');

    const cookies = await this.prisma.cookie.findMany({
      orderBy: { createdDate: 'desc' }, // 최신순 정렬
    });

    // 엔티티를 DTO로 변환
    return cookies.map(CookieResponseDto.from);
  }

  /**
   * 쿠키 단건 조회
   *
   * @param id - 쿠키 ID
   * @returns 쿠키 정보
   * @throws NotFoundException - 쿠키가 없을 때
   */
  async findOne(id: bigint): Promise<CookieResponseDto> {
    this.logger.log(`Getting cookie: ${id}`);

    const cookie = await this.prisma.cookie.findUnique({
      where: { id },
    });

    if (!cookie) {
      throw new NotFoundException(`Cookie with ID ${id} not found`);
    }

    return CookieResponseDto.from(cookie);
  }

  /**
   * 쿠키 생성
   *
   * @param siteUserId - 소유자 ID (관리자)
   * @param dto - 생성 데이터 (사용자명, 사이트명, 쿠키 문자열)
   * @returns 생성된 쿠키 정보
   *
   * 처리:
   * - isValid 기본값: true (활성 상태)
   * - siteUserId로 소유자 연결
   */
  async create(siteUserId: bigint, dto: CreateCookieDto): Promise<CookieResponseDto> {
    this.logger.log(`Creating cookie for site: ${dto.siteName}`);

    const cookie = await this.prisma.cookie.create({
      data: {
        userName: dto.userName, // 네이버 계정 사용자명
        siteName: dto.siteName, // 사이트 이름 (예: 'naver')
        cookie: dto.cookie, // 쿠키 문자열
        isValid: dto.isValid ?? true, // 유효성 (기본: 활성)
        siteUserId, // 소유자 ID
      },
    });

    this.logger.log(`Cookie created successfully: ${cookie.id}`);
    return CookieResponseDto.from(cookie);
  }

  /**
   * 쿠키 수정
   *
   * @param id - 쿠키 ID
   * @param dto - 수정 데이터 (부분 업데이트 지원)
   * @returns 수정된 쿠키 정보
   * @throws NotFoundException - 쿠키가 없을 때
   *
   * 처리:
   * - 제공된 필드만 업데이트 (null 병합 연산자 사용)
   * - 기존 값 유지 가능
   */
  async update(id: bigint, dto: UpdateCookieDto): Promise<CookieResponseDto> {
    this.logger.log(`Updating cookie: ${id}`);

    // 기존 쿠키 조회
    const existing = await this.prisma.cookie.findUnique({ where: { id } });
    if (!existing) {
      throw new NotFoundException(`Cookie with ID ${id} not found`);
    }

    // 부분 업데이트 (제공된 값만 변경, 나머지는 기존 값 유지)
    const cookie = await this.prisma.cookie.update({
      where: { id },
      data: {
        userName: dto.userName ?? existing.userName,
        siteName: dto.siteName ?? existing.siteName,
        cookie: dto.cookie ?? existing.cookie,
        isValid: dto.isValid ?? existing.isValid,
      },
    });

    this.logger.log(`Cookie updated successfully: ${id}`);
    return CookieResponseDto.from(cookie);
  }

  /**
   * 쿠키 삭제
   *
   * @param id - 쿠키 ID
   * @throws NotFoundException - 쿠키가 없을 때
   *
   * 주의: 연관된 PointUrlCookie, SavedPoint도 함께 삭제됨 (CASCADE)
   */
  async delete(id: bigint): Promise<void> {
    this.logger.log(`Deleting cookie: ${id}`);

    const existing = await this.prisma.cookie.findUnique({ where: { id } });
    if (!existing) {
      throw new NotFoundException(`Cookie with ID ${id} not found`);
    }

    await this.prisma.cookie.delete({ where: { id } });
    this.logger.log(`Cookie deleted successfully: ${id}`);
  }

  /**
   * 쿠키 유효성 토글
   *
   * @param id - 쿠키 ID
   * @returns 변경된 쿠키 정보
   * @throws NotFoundException - 쿠키가 없을 때
   *
   * 용도:
   * - 수동으로 쿠키 활성화/비활성화
   * - 포인트 수집에서 무효 쿠키는 자동 제외됨
   *
   * 토글 동작:
   * - true → false (비활성화)
   * - false → true (재활성화)
   */
  async toggleValidity(id: bigint): Promise<CookieResponseDto> {
    this.logger.log(`Toggling validity for cookie: ${id}`);

    const existing = await this.prisma.cookie.findUnique({ where: { id } });
    if (!existing) {
      throw new NotFoundException(`Cookie with ID ${id} not found`);
    }

    // 유효성 반전
    const cookie = await this.prisma.cookie.update({
      where: { id },
      data: {
        isValid: !existing.isValid,
      },
    });

    this.logger.log(`Cookie validity toggled: ${id} -> ${cookie.isValid}`);
    return CookieResponseDto.from(cookie);
  }

  /**
   * 유효한 쿠키만 조회
   *
   * @returns 유효한 쿠키 목록 (최신순)
   *
   * 용도: 포인트 수집 시 처리 대상 쿠키 조회
   * 조건: isValid = true
   */
  async findValidCookies(): Promise<CookieResponseDto[]> {
    const cookies = await this.prisma.cookie.findMany({
      where: { isValid: true }, // 유효한 쿠키만
      orderBy: { createdDate: 'desc' },
    });

    return cookies.map(CookieResponseDto.from);
  }
}

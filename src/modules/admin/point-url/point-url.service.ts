import { Injectable, NotFoundException, Logger } from '@nestjs/common';
import { PrismaService } from '../../prisma/prisma.service';
import { CreatePointUrlDto, UpdatePointUrlDto, PointUrlResponseDto, classifyUrlType } from './dto/point-url.dto';

/**
 * 포인트 URL 관리 서비스
 *
 * 크롤링으로 수집된 네이버 포인트 URL을 관리하는 CRUD 서비스
 *
 * 주요 기능:
 * 1. 포인트 URL CRUD 작업
 * 2. URL 타입 자동 분류 (NAVER, OFW_NAVER, UNSUPPORT)
 * 3. 미처리 URL 조회 (특정 쿠키에서 아직 호출하지 않은 URL)
 * 4. 영구/일회성 URL 구분 관리
 *
 * URL 타입:
 * - NAVER: campaign2-api.naver.com (네이버 캠페인 API)
 * - OFW_NAVER: ofw.adison.co/u/naverpay (외부 제휴)
 * - UNSUPPORT: 지원하지 않는 URL 형식
 */
@Injectable()
export class PointUrlService {
  private readonly logger = new Logger(PointUrlService.name);

  constructor(private readonly prisma: PrismaService) {}

  /**
   * 모든 포인트 URL 조회
   *
   * @returns URL 목록 (최신순)
   *
   * 용도: 관리자 화면에서 전체 URL 목록 표시
   */
  async findAll(): Promise<PointUrlResponseDto[]> {
    this.logger.log('Getting all point URLs');

    const pointUrls = await this.prisma.pointUrl.findMany({
      orderBy: { createdDate: 'desc' },
    });

    return pointUrls.map(PointUrlResponseDto.from);
  }

  /**
   * 포인트 URL 단건 조회
   *
   * @param id - URL ID
   * @returns URL 정보
   * @throws NotFoundException - URL이 없을 때
   */
  async findOne(id: bigint): Promise<PointUrlResponseDto> {
    this.logger.log(`Getting point URL: ${id}`);

    const pointUrl = await this.prisma.pointUrl.findUnique({
      where: { id },
    });

    if (!pointUrl) {
      throw new NotFoundException(`PointUrl with ID ${id} not found`);
    }

    return PointUrlResponseDto.from(pointUrl);
  }

  /**
   * URL 문자열로 포인트 URL 조회
   *
   * @param url - URL 문자열
   * @returns URL 엔티티 또는 null
   *
   * 용도: 중복 URL 체크, URL 존재 여부 확인
   */
  async findByUrl(url: string) {
    return this.prisma.pointUrl.findFirst({
      where: { url },
    });
  }

  /**
   * 포인트 URL 생성
   *
   * @param dto - 생성 데이터 (URL, permanent 플래그)
   * @returns 생성된 URL 정보
   *
   * 처리:
   * - URL 타입 자동 분류 (classifyUrlType)
   * - permanent 기본값: false (일회성)
   */
  async create(dto: CreatePointUrlDto): Promise<PointUrlResponseDto> {
    this.logger.log(`Creating point URL: ${dto.url}`);

    // URL 타입 자동 분류
    const pointUrlType = classifyUrlType(dto.url);

    const pointUrl = await this.prisma.pointUrl.create({
      data: {
        url: dto.url,
        name: pointUrlType, // 타입을 이름으로 사용
        pointUrlType, // 분류된 URL 타입
        permanent: dto.permanent ?? false, // 영구 URL 여부 (기본: 일회성)
      },
    });

    this.logger.log(`Point URL created successfully: ${pointUrl.id}`);
    return PointUrlResponseDto.from(pointUrl);
  }

  /**
   * 포인트 URL 벌크 생성
   *
   * @param urls - URL 문자열 배열
   * @returns 새로 생성된 URL 개수
   *
   * 처리:
   * 1. 기존 URL 조회 (중복 체크)
   * 2. 신규 URL만 필터링
   * 3. 각 URL 타입 자동 분류
   * 4. 벌크 INSERT (skipDuplicates)
   *
   * 용도: 크롤링 후 수집된 URL 일괄 저장
   */
  async createMany(urls: string[]): Promise<number> {
    // 1. 기존 URL 조회
    const existingUrls = await this.prisma.pointUrl.findMany({
      where: { url: { in: urls } },
      select: { url: true },
    });

    const existingUrlSet = new Set(existingUrls.map((p) => p.url));

    // 2. 신규 URL만 필터링
    const newUrls = urls.filter((url) => !existingUrlSet.has(url));

    if (newUrls.length === 0) {
      return 0;
    }

    // 3. 저장 데이터 준비 (URL 타입 자동 분류)
    const data = newUrls.map((url) => {
      const pointUrlType = classifyUrlType(url);
      return {
        url,
        name: pointUrlType,
        pointUrlType,
        permanent: false,
      };
    });

    // 4. 벌크 INSERT
    const result = await this.prisma.pointUrl.createMany({
      data,
      skipDuplicates: true,
    });

    this.logger.log(`Created ${result.count} new point URLs`);
    return result.count;
  }

  /**
   * 포인트 URL 수정
   *
   * @param id - URL ID
   * @param dto - 수정 데이터 (URL, permanent)
   * @returns 수정된 URL 정보
   * @throws NotFoundException - URL이 없을 때
   *
   * 처리:
   * - URL 변경 시 타입 재분류
   * - 부분 업데이트 지원
   */
  async update(id: bigint, dto: UpdatePointUrlDto): Promise<PointUrlResponseDto> {
    this.logger.log(`Updating point URL: ${id}`);

    const existing = await this.prisma.pointUrl.findUnique({ where: { id } });
    if (!existing) {
      throw new NotFoundException(`PointUrl with ID ${id} not found`);
    }

    // URL이 변경되면 타입 재분류
    const url = dto.url ?? existing.url;
    const pointUrlType = dto.url ? classifyUrlType(dto.url) : existing.pointUrlType;

    const pointUrl = await this.prisma.pointUrl.update({
      where: { id },
      data: {
        url,
        name: pointUrlType ?? existing.name,
        pointUrlType,
        permanent: dto.permanent ?? existing.permanent,
      },
    });

    this.logger.log(`Point URL updated successfully: ${id}`);
    return PointUrlResponseDto.from(pointUrl);
  }

  /**
   * 포인트 URL 삭제
   *
   * @param id - URL ID
   * @throws NotFoundException - URL이 없을 때
   *
   * 주의: 연관된 PointUrlCookie도 함께 삭제됨
   */
  async delete(id: bigint): Promise<void> {
    this.logger.log(`Deleting point URL: ${id}`);

    const existing = await this.prisma.pointUrl.findUnique({ where: { id } });
    if (!existing) {
      throw new NotFoundException(`PointUrl with ID ${id} not found`);
    }

    await this.prisma.pointUrl.delete({ where: { id } });
    this.logger.log(`Point URL deleted successfully: ${id}`);
  }

  /**
   * 영구 URL 플래그 토글
   *
   * @param id - URL ID
   * @returns 변경된 URL 정보
   * @throws NotFoundException - URL이 없을 때
   *
   * 영구 URL (permanent = true):
   * - 삭제되지 않고 계속 유지되는 URL
   * - 매일 반복 적립 가능한 URL에 사용
   *
   * 토글 동작:
   * - false → true (영구로 변경)
   * - true → false (일회성으로 변경)
   */
  async togglePermanent(id: bigint): Promise<PointUrlResponseDto> {
    this.logger.log(`Toggling permanent status for point URL: ${id}`);

    const existing = await this.prisma.pointUrl.findUnique({ where: { id } });
    if (!existing) {
      throw new NotFoundException(`PointUrl with ID ${id} not found`);
    }

    const pointUrl = await this.prisma.pointUrl.update({
      where: { id },
      data: {
        permanent: !existing.permanent,
      },
    });

    this.logger.log(`Point URL permanent status toggled: ${id} -> ${pointUrl.permanent}`);
    return PointUrlResponseDto.from(pointUrl);
  }

  /**
   * 특정 쿠키에서 미처리된 URL 조회
   *
   * @param cookieId - 쿠키 ID
   * @returns 미처리 URL 목록
   *
   * 처리 흐름:
   * 1. 해당 쿠키로 이미 호출한 URL 조회 (PointUrlCookie)
   * 2. 처리된 URL ID 목록 추출
   * 3. 처리되지 않은 URL 중 지원 타입만 반환
   *
   * 필터 조건:
   * - 해당 쿠키에서 호출하지 않은 URL
   * - 지원 타입만 (NAVER, OFW_NAVER)
   * - UNSUPPORT 타입 제외
   */
  async findUnprocessedUrls(cookieId: bigint) {
    // 1. 해당 쿠키로 이미 처리한 URL ID 조회
    const processedUrls = await this.prisma.pointUrlCookie.findMany({
      where: { cookieId },
      select: { pointUrlId: true },
    });

    const processedUrlIds = processedUrls.map((p) => p.pointUrlId);

    // 2. 미처리 URL 중 지원 타입만 조회
    return this.prisma.pointUrl.findMany({
      where: {
        id: { notIn: processedUrlIds }, // 처리되지 않은 URL
        pointUrlType: { in: ['NAVER', 'OFW_NAVER'] }, // 지원 타입만
      },
      orderBy: { createdDate: 'desc' },
    });
  }

  /**
   * 날짜 범위로 포인트 URL 조회
   *
   * @param start - 시작 날짜
   * @param end - 종료 날짜
   * @returns 해당 기간에 생성된 URL 목록
   *
   * 용도: 통계, 리포트용 날짜 기반 조회
   */
  async findByCreatedDateBetween(start: Date, end: Date) {
    return this.prisma.pointUrl.findMany({
      where: {
        createdDate: {
          gte: start, // 시작 날짜 이상
          lte: end, // 종료 날짜 이하
        },
      },
    });
  }
}

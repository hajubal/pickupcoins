import { Injectable, Logger } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import { ClienCrawler } from './crawlers/clien.crawler';
import { RuliwebCrawler } from './crawlers/ruliweb.crawler';
import { BaseCrawler, CrawledUrl } from './crawlers/base.crawler';
import { classifyUrlType } from '../admin/point-url/dto/point-url.dto';

/**
 * 크롤러 오케스트레이션 서비스
 *
 * 여러 커뮤니티 사이트에서 네이버 포인트 URL을 수집하고 DB에 저장하는 서비스
 *
 * 주요 기능:
 * 1. 등록된 모든 크롤러 실행 관리
 * 2. 수집된 URL 중복 제거 및 신규 URL만 저장
 * 3. URL 타입 자동 분류 (NAVER, OFW_NAVER, UNSUPPORT)
 * 4. 개별 사이트 크롤링 지원
 *
 * 지원 사이트:
 * - Clien (클리앙)
 * - Ruliweb (루리웹)
 */
@Injectable()
export class CrawlerService {
  private readonly logger = new Logger(CrawlerService.name);

  // 등록된 크롤러 목록
  private readonly crawlers: BaseCrawler[];

  constructor(
    private readonly prisma: PrismaService,
    private readonly clienCrawler: ClienCrawler,
    private readonly ruliwebCrawler: RuliwebCrawler,
  ) {
    // 크롤러 인스턴스들을 배열로 관리
    this.crawlers = [clienCrawler, ruliwebCrawler];
  }

  /**
   * 전체 크롤링 실행 및 신규 URL 저장
   *
   * @returns 새로 저장된 URL 개수
   *
   * 처리 흐름:
   * 1. crawlAllSites()로 모든 사이트에서 URL 수집
   * 2. saveNewUrls()로 중복 제거 후 신규 URL만 저장
   *
   * 스케줄러에서 주기적으로 호출됨 (기본 5분 간격)
   */
  async savingPointUrl(): Promise<number> {
    const allUrls = await this.crawlAllSites();
    return this.saveNewUrls(allUrls);
  }

  /**
   * 모든 등록된 사이트 크롤링
   *
   * @returns 수집된 모든 URL 배열
   *
   * 처리 흐름:
   * 1. 각 크롤러에서 사이트 정보 조회
   * 2. DB에서 해당 사이트의 URL 조회
   * 3. 크롤러 실행하여 포인트 URL 수집
   * 4. 모든 결과 병합
   *
   * 에러 처리:
   * - 개별 크롤러 실패 시 로그 기록 후 계속 진행
   * - 사이트가 DB에 없으면 해당 크롤러 건너뜀
   */
  private async crawlAllSites(): Promise<CrawledUrl[]> {
    const allUrls: CrawledUrl[] = [];

    for (const crawler of this.crawlers) {
      try {
        // 크롤러에서 사이트 메타 정보 조회
        const siteData = crawler.getSiteData();

        // DB에서 사이트 정보 조회 (URL 등)
        const site = await this.prisma.site.findFirst({
          where: { name: siteData.siteName },
        });

        // 사이트가 DB에 등록되어 있지 않으면 건너뜀
        if (!site) {
          this.logger.warn(`Site not found: ${siteData.siteName}`);
          continue;
        }

        // 크롤링 실행 및 결과 수집
        const urls = await crawler.crawl(site.url);
        allUrls.push(...urls);
      } catch (error) {
        // 개별 크롤러 실패 시 로그만 남기고 계속 진행
        this.logger.error(`Crawling failed for crawler`, error);
      }
    }

    return allUrls;
  }

  /**
   * 신규 URL만 필터링하여 DB에 저장
   *
   * @param urls - 수집된 URL 배열
   * @returns 새로 저장된 URL 개수
   *
   * 처리 흐름:
   * 1. URL 중복 제거 (Set 사용)
   * 2. DB에서 이미 존재하는 URL 조회
   * 3. 신규 URL만 필터링
   * 4. URL 타입 분류 (NAVER, OFW_NAVER, UNSUPPORT)
   * 5. 벌크 INSERT 실행
   *
   * URL 타입 분류 기준:
   * - NAVER: campaign2-api.naver.com 포함
   * - OFW_NAVER: ofw.adison.co/u/naverpay 포함
   * - UNSUPPORT: 그 외 (처리 대상 아님)
   */
  private async saveNewUrls(urls: CrawledUrl[]): Promise<number> {
    // 1. URL 문자열 중복 제거
    const uniqueUrls = [...new Set(urls.map((u) => u.url))];

    // 2. DB에서 이미 존재하는 URL 조회
    const existingUrls = await this.prisma.pointUrl.findMany({
      where: { url: { in: uniqueUrls } },
      select: { url: true },
    });
    const existingUrlSet = new Set(existingUrls.map((p) => p.url));

    // 3. 신규 URL만 필터링
    const newUrls = uniqueUrls.filter((url) => !existingUrlSet.has(url));

    // 신규 URL이 없으면 종료
    if (newUrls.length === 0) {
      this.logger.log('No new point URLs found');
      return 0;
    }

    // 4. 저장할 데이터 준비 (URL 타입 자동 분류)
    const data = newUrls.map((url) => {
      const pointUrlType = classifyUrlType(url);
      return {
        url,
        name: pointUrlType,           // 타입을 이름으로 사용
        pointUrlType,                  // URL 타입 (NAVER, OFW_NAVER, UNSUPPORT)
        permanent: false,              // 기본값: 일회성 URL
      };
    });

    // 5. 벌크 INSERT (skipDuplicates로 중복 무시)
    const result = await this.prisma.pointUrl.createMany({
      data,
      skipDuplicates: true,
    });

    this.logger.log(`Saved ${result.count} new point URLs`);
    return result.count;
  }

  /**
   * 특정 사이트만 크롤링
   *
   * @param siteName - 크롤링할 사이트 이름 (예: 'clien', 'ruliweb')
   * @returns 수집된 URL 배열
   * @throws Error - 크롤러 또는 사이트를 찾을 수 없을 때
   *
   * 용도: 수동 크롤링 트리거, 테스트, 디버깅
   */
  async crawlSite(siteName: string): Promise<CrawledUrl[]> {
    // 해당 사이트의 크롤러 찾기
    const crawler = this.crawlers.find((c) => c.getSiteData().siteName === siteName);

    if (!crawler) {
      throw new Error(`Crawler not found for site: ${siteName}`);
    }

    // 크롤러에서 사이트 메타 정보 조회
    const siteData = crawler.getSiteData();

    // DB에서 사이트 URL 조회
    const site = await this.prisma.site.findFirst({
      where: { name: siteData.siteName },
    });

    if (!site) {
      throw new Error(`Site not found in database: ${siteName}`);
    }

    // 크롤링 실행
    return crawler.crawl(site.url);
  }

  /**
   * 등록된 크롤러 목록 조회
   *
   * @returns 크롤러 정보 배열 (사이트 이름, 도메인 등)
   *
   * 용도: 관리자 화면에서 지원 사이트 목록 표시
   */
  getCrawlerInfo() {
    return this.crawlers.map((c) => c.getSiteData());
  }
}

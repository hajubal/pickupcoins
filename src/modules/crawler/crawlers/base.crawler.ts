import axios from 'axios';
import * as cheerio from 'cheerio';
import { Logger } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';

/**
 * 사이트 메타 정보 인터페이스
 * - siteName: 사이트 식별자 (예: 'clien', 'ruliweb')
 * - domain: 사이트 도메인 (예: 'clien.net')
 * - boardUrl: 게시판 URL (크롤링 시작점)
 */
export interface SiteData {
  siteName: string;
  domain: string;
  boardUrl: string;
}

/**
 * 크롤링 결과 URL 인터페이스
 * - url: 수집된 네이버 포인트 URL
 */
export interface CrawledUrl {
  url: string;
}

/**
 * 크롤러 베이스 클래스 (추상 클래스)
 *
 * 커뮤니티 사이트에서 네이버 포인트 URL을 수집하는 크롤러의 공통 로직
 *
 * 상속 구조:
 * - BaseCrawler (공통 로직)
 *   ├── ClienCrawler (클리앙 특화 구현)
 *   └── RuliwebCrawler (루리웹 특화 구현)
 *
 * 크롤링 흐름:
 * 1. 게시판 목록 페이지에서 게시글 URL 수집 (fetchPostUrls)
 * 2. 각 게시글에서 네이버 포인트 URL 추출 (extractPointUrls)
 * 3. 포인트 URL 유효성 검증 (isNaverPointUrl)
 *
 * 서브클래스가 구현해야 할 추상 메서드:
 * - getSiteData(): 사이트 메타 정보 반환
 * - getArticleSelector(): 게시글 내 링크 추출용 CSS 셀렉터
 * - fetchPostUrls(): 게시판에서 게시글 URL 목록 추출
 */
export abstract class BaseCrawler {
  protected readonly logger: Logger;

  // HTTP 요청 설정
  protected readonly timeout: number; // 요청 타임아웃 (ms)
  protected readonly retryCount: number; // 재시도 횟수
  protected readonly userAgent: string; // User-Agent 헤더

  constructor(
    protected readonly configService: ConfigService,
    loggerContext: string, // 로거 컨텍스트 (클래스명)
  ) {
    this.logger = new Logger(loggerContext);

    // 설정에서 값 로드 (기본값 제공)
    this.timeout = this.configService.get<number>('crawler.timeout') || 10000; // 10초
    this.retryCount = this.configService.get<number>('crawler.retryCount') || 3; // 3회
    this.userAgent =
      this.configService.get<string>('naver.userAgent') ||
      'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36';
  }

  /**
   * [추상 메서드] 사이트 메타 정보 반환
   *
   * @returns SiteData - 사이트 이름, 도메인, 게시판 URL
   *
   * 서브클래스에서 각 사이트에 맞게 구현
   */
  abstract getSiteData(): SiteData;

  /**
   * [추상 메서드] 게시글 내 링크 추출용 CSS 셀렉터
   *
   * @returns CSS 셀렉터 문자열
   *
   * 서브클래스에서 각 사이트의 HTML 구조에 맞게 구현
   * 예: 'a[href]', '.article-content a'
   */
  abstract getArticleSelector(): string;

  /**
   * [추상 메서드] 게시판에서 게시글 URL 목록 추출
   *
   * @param siteUrl - 게시판 URL
   * @returns 게시글 URL Set
   *
   * 서브클래스에서 각 사이트의 게시판 구조에 맞게 구현
   */
  abstract fetchPostUrls(siteUrl: string): Promise<Set<string>>;

  /**
   * 메인 크롤링 실행
   *
   * @param siteUrl - 크롤링 시작 URL (게시판 URL)
   * @returns 수집된 포인트 URL 배열
   *
   * 처리 흐름:
   * 1. fetchPostUrls()로 게시글 URL 목록 수집
   * 2. extractPointUrls()로 각 게시글에서 포인트 URL 추출
   * 3. 결과 반환
   *
   * 에러 처리:
   * - 전체 크롤링 실패 시 빈 배열 반환
   */
  async crawl(siteUrl: string): Promise<CrawledUrl[]> {
    const siteData = this.getSiteData();
    this.logger.log(`Starting crawl for ${siteData.siteName}`);

    try {
      // 1. 게시판에서 게시글 URL 목록 수집
      const postUrls = await this.fetchPostUrls(siteUrl);

      // 2. 각 게시글에서 포인트 URL 추출
      const pointUrls = await this.extractPointUrls(siteData.domain, postUrls);

      this.logger.log(
        `Crawling completed. site: ${siteData.siteName}, posts: ${postUrls.size}, points: ${pointUrls.length}`,
      );

      return pointUrls;
    } catch (error) {
      this.logger.error(`Crawling failed for ${siteData.siteName}`, error);
      return [];
    }
  }

  /**
   * 게시글들에서 네이버 포인트 URL 추출
   *
   * @param domain - 사이트 도메인 (상대 URL 변환용)
   * @param postUrls - 게시글 URL Set
   * @returns 추출된 포인트 URL 배열
   *
   * 처리 흐름:
   * 1. 각 게시글 URL에 HTTP 요청
   * 2. HTML 파싱 (cheerio 사용)
   * 3. getArticleSelector()로 지정된 링크들 추출
   * 4. isNaverPointUrl()로 포인트 URL만 필터링
   *
   * 에러 처리:
   * - 개별 게시글 요청 실패 시 로그 기록 후 계속 진행
   */
  protected async extractPointUrls(domain: string, postUrls: Set<string>): Promise<CrawledUrl[]> {
    const pointUrls: CrawledUrl[] = [];

    for (const url of postUrls) {
      try {
        // 상대 URL이면 도메인 붙이기
        const fullUrl = url.startsWith('http') ? url : `${domain}${url}`;

        // HTTP GET 요청
        const response = await axios.get(fullUrl, {
          timeout: this.timeout,
          headers: {
            'User-Agent': this.userAgent,
          },
        });

        // HTML 파싱
        const $ = cheerio.load(response.data);
        const selector = this.getArticleSelector();

        // 링크 추출 및 포인트 URL 필터링
        $(selector).each((_, element) => {
          const href = $(element).attr('href');
          if (href && this.isNaverPointUrl(href)) {
            this.logger.debug(`Found point URL: ${href}`);
            pointUrls.push({ url: href });
          }
        });
      } catch (error) {
        // 개별 게시글 실패 시 로그만 남기고 계속 진행
        this.logger.warn(`Failed to crawl URL: ${domain}${url}. Error: ${(error as Error).message}`);
      }
    }

    return pointUrls;
  }

  /**
   * 네이버 포인트 URL 여부 판단
   *
   * @param url - 검사할 URL
   * @returns true: 네이버 포인트 URL, false: 아님
   *
   * 판단 기준 (OR 조건):
   * - 'naver.com/point' 포함
   * - 'naver.me' 포함
   * - 'm.site.naver.com' 포함
   */
  protected isNaverPointUrl(url: string): boolean {
    return (
      url != null && (url.includes('naver.com/point') || url.includes('naver.me') || url.includes('m.site.naver.com'))
    );
  }

  /**
   * HTTP 요청 (재시도 지원)
   *
   * @param url - 요청 URL
   * @param retries - 재시도 횟수 (기본값: 설정값)
   * @returns 응답 본문 (HTML 문자열)
   * @throws Error - 모든 재시도 실패 시
   *
   * 재시도 전략:
   * - 실패 시 지수 백오프 (1초, 2초, 3초...)
   * - 마지막 시도에서 실패하면 에러 throw
   */
  protected async fetchWithRetry(url: string, retries = this.retryCount): Promise<string> {
    for (let attempt = 1; attempt <= retries; attempt++) {
      try {
        const response = await axios.get(url, {
          timeout: this.timeout,
          headers: {
            'User-Agent': this.userAgent,
          },
        });
        return response.data;
      } catch (error) {
        // 마지막 시도에서 실패하면 에러 throw
        if (attempt === retries) {
          throw error;
        }
        // 재시도 전 대기 (지수 백오프)
        this.logger.warn(`Retry ${attempt}/${retries} for URL: ${url}`);
        await new Promise((resolve) => setTimeout(resolve, 1000 * attempt));
      }
    }
    throw new Error(`Failed to fetch after ${retries} retries`);
  }
}

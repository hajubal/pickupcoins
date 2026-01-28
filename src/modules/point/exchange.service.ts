import { Injectable, Logger } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import axios, { AxiosResponse } from 'axios';
import { PrismaService } from '../prisma/prisma.service';
import { PointUrl } from '@prisma/client';

/**
 * 포인트 교환 요청 DTO
 * - cookieId: 사용할 쿠키 ID
 * - userName: 사용자 이름 (로깅용)
 * - siteName: 사이트 이름 (로깅용)
 * - cookie: 실제 쿠키 문자열
 * - webHookUrl: Slack 알림 URL (선택)
 */
export interface ExchangeDto {
  cookieId: bigint;
  userName: string;
  siteName: string;
  cookie: string;
  webHookUrl?: string | null;
}

/**
 * 포인트 교환 결과 인터페이스
 * - success: 포인트 적립 성공 여부
 * - pointsSaved: 적립된 포인트 금액
 * - invalidCookie: 쿠키 무효화 여부 (로그인 필요 시 true)
 * - error: 에러 메시지 (실패 시)
 */
export interface ExchangeResult {
  success: boolean;
  pointsSaved: number;
  invalidCookie: boolean;
  error?: string;
}

/**
 * 포인트 교환 서비스
 *
 * 네이버 포인트 URL을 호출하여 포인트를 적립하는 핵심 서비스
 *
 * 주요 기능:
 * 1. 포인트 URL HTTP 요청 실행
 * 2. 응답 분석하여 적립 성공/실패 판단
 * 3. 쿠키 유효성 검증 및 무효화 처리
 * 4. 적립 기록 저장
 *
 * 응답 분석 키워드:
 * - '적립' : 포인트 적립 성공
 * - '로그인이 필요' : 쿠키 만료, 무효화 처리 필요
 */
@Injectable()
export class ExchangeService {
  private readonly logger = new Logger(ExchangeService.name);

  // 응답 분석에 사용되는 키워드 및 패턴
  private readonly saveKeyword: string; // 포인트 적립 성공 키워드
  private readonly invalidCookieKeyword: string; // 쿠키 무효 키워드
  private readonly amountPattern: RegExp; // 포인트 금액 추출 정규식
  private readonly userAgent: string; // HTTP 요청 User-Agent

  constructor(
    private readonly prisma: PrismaService,
    private readonly configService: ConfigService,
  ) {
    // 설정에서 키워드 및 패턴 로드 (기본값 제공)
    this.saveKeyword = this.configService.get<string>('naver.saveKeyword') || '적립';
    this.invalidCookieKeyword = this.configService.get<string>('naver.invalidCookieKeyword') || '로그인이 필요';
    this.amountPattern = new RegExp(
      this.configService.get<string>('naver.amountPattern') || '\\s\\d+원이 적립 됩니다.',
    );
    this.userAgent =
      this.configService.get<string>('naver.userAgent') ||
      'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36';
  }

  /**
   * 포인트 URL 호출 및 포인트 적립 처리
   *
   * @param pointUrl - 호출할 포인트 URL 엔티티
   * @param exchangeDto - 교환 요청 정보 (쿠키, 사용자 정보 등)
   * @returns ExchangeResult - 적립 결과
   *
   * 처리 흐름:
   * 1. 포인트 URL에 HTTP GET 요청 (쿠키 헤더 포함)
   * 2. 응답 본문 분석 (processResponse)
   * 3. 호출 로그 저장 (saveLog)
   * 4. 결과 반환
   *
   * 에러 처리:
   * - 네트워크 오류, 타임아웃 등은 error 필드에 메시지 저장
   * - 빈 응답은 'Empty response' 에러로 처리
   */
  async exchange(pointUrl: PointUrl, exchangeDto: ExchangeDto): Promise<ExchangeResult> {
    this.logger.debug(`Calling point URL. url: ${pointUrl.url}, user: ${exchangeDto.userName}`);

    try {
      // HTTP GET 요청 실행 (쿠키와 User-Agent 헤더 포함)
      const response = await axios.get(pointUrl.url, {
        headers: {
          Cookie: exchangeDto.cookie,
          'User-Agent': this.userAgent,
        },
        timeout: 30000, // 30초 타임아웃
        maxRedirects: 5, // 최대 5회 리다이렉트
      });

      const body = response.data as string;

      // 빈 응답 처리
      if (!body) {
        this.logger.warn(`Exchange response is empty. url: ${pointUrl.url}`);
        return { success: false, pointsSaved: 0, invalidCookie: false, error: 'Empty response' };
      }

      // 응답 분석 및 처리
      const result = await this.processResponse(body, exchangeDto, response, pointUrl);

      // 호출 로그 저장 (URL-쿠키 매핑 및 상세 로그)
      await this.saveLog(pointUrl, exchangeDto, response);

      this.logger.log(`Point exchange completed. user: ${exchangeDto.userName}, url: ${pointUrl.url}`);
      return result;
    } catch (error) {
      this.logger.error(`Failed to exchange point. url: ${pointUrl.url}, user: ${exchangeDto.userName}`, error);
      return {
        success: false,
        pointsSaved: 0,
        invalidCookie: false,
        error: (error as Error).message,
      };
    }
  }

  /**
   * 응답 본문 분석 및 후속 처리
   *
   * @param body - HTTP 응답 본문
   * @param exchangeDto - 교환 요청 정보
   * @param response - Axios 응답 객체
   * @param pointUrl - 포인트 URL 엔티티
   * @returns ExchangeResult
   *
   * 분석 우선순위:
   * 1. 쿠키 무효 확인 ('로그인이 필요' 키워드)
   *    → 쿠키 무효화 처리 후 invalidCookie: true 반환
   * 2. 포인트 적립 확인 ('적립' 키워드)
   *    → 포인트 기록 저장 후 success: true 반환
   * 3. 해당 없음
   *    → success: false 반환 (이미 참여했거나 기타 사유)
   */
  private async processResponse(
    body: string,
    exchangeDto: ExchangeDto,
    response: AxiosResponse,
    _pointUrl: PointUrl,
  ): Promise<ExchangeResult> {
    // 1. 쿠키 무효 여부 확인 (최우선 처리)
    if (this.isInvalidCookie(body)) {
      this.logger.warn(`Cookie is invalid. user: ${exchangeDto.userName}`);
      await this.invalidateCookie(exchangeDto.cookieId);
      return { success: false, pointsSaved: 0, invalidCookie: true };
    }

    // 2. 포인트 적립 성공 여부 확인
    if (this.isSavePoint(body)) {
      this.logger.debug(`Point saved successfully. user: ${exchangeDto.userName}`);
      const amount = await this.savePointPostProcess(exchangeDto, response);
      return { success: true, pointsSaved: amount, invalidCookie: false };
    }

    // 3. 해당 없음 (이미 참여, 기간 만료 등)
    this.logger.debug(`No point action needed. user: ${exchangeDto.userName}`);
    return { success: false, pointsSaved: 0, invalidCookie: false };
  }

  /**
   * 포인트 적립 성공 여부 판단
   *
   * @param content - 응답 본문
   * @returns true: 적립 성공, false: 적립 실패/해당 없음
   *
   * 판단 기준: 응답에 '적립' 키워드 포함 여부
   */
  private isSavePoint(content: string): boolean {
    return content.includes(this.saveKeyword);
  }

  /**
   * 쿠키 무효 여부 판단
   *
   * @param content - 응답 본문
   * @returns true: 쿠키 무효 (로그인 필요), false: 쿠키 유효
   *
   * 판단 기준: 응답에 '로그인이 필요' 키워드 포함 여부
   */
  private isInvalidCookie(content: string): boolean {
    return content.includes(this.invalidCookieKeyword);
  }

  /**
   * 쿠키 무효화 처리
   *
   * @param cookieId - 무효화할 쿠키 ID
   *
   * DB에서 해당 쿠키의 isValid를 false로 업데이트
   * → 이후 포인트 수집에서 해당 쿠키 제외됨
   */
  private async invalidateCookie(cookieId: bigint): Promise<void> {
    await this.prisma.cookie.update({
      where: { id: cookieId },
      data: { isValid: false },
    });
  }

  /**
   * 포인트 적립 성공 후 처리
   *
   * @param exchangeDto - 교환 요청 정보
   * @param response - Axios 응답 객체
   * @returns 적립된 포인트 금액
   *
   * 처리 내용:
   * 1. Set-Cookie 헤더가 있으면 쿠키 업데이트 (세션 유지)
   * 2. 응답에서 포인트 금액 추출
   * 3. SavedPoint 테이블에 적립 기록 저장
   */
  private async savePointPostProcess(exchangeDto: ExchangeDto, response: AxiosResponse): Promise<number> {
    // 1. Set-Cookie 헤더 처리 (서버에서 새 쿠키 발급 시 업데이트)
    const setCookie = response.headers['set-cookie'];
    if (setCookie && setCookie.length > 0) {
      this.logger.debug(`Updating cookie. user: ${exchangeDto.userName}`);
      await this.prisma.cookie.update({
        where: { id: exchangeDto.cookieId },
        data: { cookie: setCookie.join('; ') },
      });
    }

    // 2. 응답에서 포인트 금액 추출
    const amount = this.extractAmount(response.data as string);

    // 3. 포인트 적립 기록 저장
    await this.prisma.savedPoint.create({
      data: {
        cookieId: exchangeDto.cookieId,
        amount,
        responseBody: response.data as string,
      },
    });

    this.logger.log(`Point saved. user: ${exchangeDto.userName}, amount: ${amount}원`);
    return amount;
  }

  /**
   * 응답 본문에서 포인트 금액 추출
   *
   * @param body - 응답 본문
   * @returns 추출된 포인트 금액, 추출 실패 시 0
   *
   * 추출 패턴: '\s\d+원이 적립 됩니다.'
   * 예: ' 10원이 적립 됩니다.' → 10
   */
  private extractAmount(body: string): number {
    const match = body.match(this.amountPattern);
    if (match) {
      // '10원이 적립 됩니다.' → '10'
      const amountStr = match[0].replace('원이 적립 됩니다.', '').trim();
      const amount = parseInt(amountStr, 10);
      if (!isNaN(amount)) {
        return amount;
      }
    }
    this.logger.warn(`Failed to extract amount from response body`);
    return 0;
  }

  /**
   * 호출 로그 저장
   *
   * @param pointUrl - 포인트 URL 엔티티
   * @param exchangeDto - 교환 요청 정보
   * @param response - Axios 응답 객체
   *
   * 저장 내용:
   * 1. PointUrlCookie: URL과 쿠키 간 처리 완료 관계 기록
   *    → 동일 URL을 같은 쿠키로 중복 호출 방지
   * 2. PointUrlCallLog: 상세 호출 로그 (디버깅/분석용)
   *    → URL, 사용자, 응답 본문/헤더, 상태 코드 등
   */
  private async saveLog(pointUrl: PointUrl, exchangeDto: ExchangeDto, response: AxiosResponse): Promise<void> {
    // 1. URL-쿠키 매핑 저장 (중복 호출 방지용)
    await this.prisma.pointUrlCookie.create({
      data: {
        pointUrlId: pointUrl.id,
        cookieId: exchangeDto.cookieId,
      },
    });

    // 2. 상세 호출 로그 저장
    await this.prisma.pointUrlCallLog.create({
      data: {
        pointUrl: pointUrl.url,
        siteName: exchangeDto.siteName,
        userName: exchangeDto.userName,
        responseBody: response.data as string,
        responseHeader: JSON.stringify(response.headers),
        cookie: exchangeDto.cookie,
        responseStatusCode: response.status,
      },
    });

    this.logger.debug(`Exchange log saved. user: ${exchangeDto.userName}, url: ${pointUrl.url}`);
  }
}

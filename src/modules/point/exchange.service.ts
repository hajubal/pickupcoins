import { Injectable, Logger } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import axios, { AxiosResponse } from 'axios';
import { PrismaService } from '../prisma/prisma.service';
import { PointUrl, Cookie } from '@prisma/client';

export interface ExchangeDto {
  cookieId: bigint;
  userName: string;
  siteName: string;
  cookie: string;
  webHookUrl?: string | null;
}

export interface ExchangeResult {
  success: boolean;
  pointsSaved: number;
  invalidCookie: boolean;
  error?: string;
}

@Injectable()
export class ExchangeService {
  private readonly logger = new Logger(ExchangeService.name);
  private readonly saveKeyword: string;
  private readonly invalidCookieKeyword: string;
  private readonly amountPattern: RegExp;
  private readonly userAgent: string;

  constructor(
    private readonly prisma: PrismaService,
    private readonly configService: ConfigService,
  ) {
    this.saveKeyword = this.configService.get<string>('naver.saveKeyword') || '적립';
    this.invalidCookieKeyword = this.configService.get<string>('naver.invalidCookieKeyword') || '로그인이 필요';
    this.amountPattern = new RegExp(this.configService.get<string>('naver.amountPattern') || '\\s\\d+원이 적립 됩니다.');
    this.userAgent =
      this.configService.get<string>('naver.userAgent') ||
      'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36';
  }

  /**
   * Execute point URL and handle response
   */
  async exchange(pointUrl: PointUrl, exchangeDto: ExchangeDto): Promise<ExchangeResult> {
    this.logger.debug(`Calling point URL. url: ${pointUrl.url}, user: ${exchangeDto.userName}`);

    try {
      const response = await axios.get(pointUrl.url, {
        headers: {
          Cookie: exchangeDto.cookie,
          'User-Agent': this.userAgent,
        },
        timeout: 30000,
        maxRedirects: 5,
      });

      const body = response.data as string;
      if (!body) {
        this.logger.warn(`Exchange response is empty. url: ${pointUrl.url}`);
        return { success: false, pointsSaved: 0, invalidCookie: false, error: 'Empty response' };
      }

      const result = await this.processResponse(body, exchangeDto, response, pointUrl);
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
   * Process response and determine action
   */
  private async processResponse(
    body: string,
    exchangeDto: ExchangeDto,
    response: AxiosResponse,
    pointUrl: PointUrl,
  ): Promise<ExchangeResult> {
    if (this.isInvalidCookie(body)) {
      this.logger.warn(`Cookie is invalid. user: ${exchangeDto.userName}`);
      await this.invalidateCookie(exchangeDto.cookieId);
      return { success: false, pointsSaved: 0, invalidCookie: true };
    }

    if (this.isSavePoint(body)) {
      this.logger.debug(`Point saved successfully. user: ${exchangeDto.userName}`);
      const amount = await this.savePointPostProcess(exchangeDto, response);
      return { success: true, pointsSaved: amount, invalidCookie: false };
    }

    this.logger.debug(`No point action needed. user: ${exchangeDto.userName}`);
    return { success: false, pointsSaved: 0, invalidCookie: false };
  }

  /**
   * Check if point was saved
   */
  private isSavePoint(content: string): boolean {
    return content.includes(this.saveKeyword);
  }

  /**
   * Check if cookie is invalid
   */
  private isInvalidCookie(content: string): boolean {
    return content.includes(this.invalidCookieKeyword);
  }

  /**
   * Invalidate cookie in database
   */
  private async invalidateCookie(cookieId: bigint): Promise<void> {
    await this.prisma.cookie.update({
      where: { id: cookieId },
      data: { isValid: false },
    });
  }

  /**
   * Process after successful point save
   */
  private async savePointPostProcess(exchangeDto: ExchangeDto, response: AxiosResponse): Promise<number> {
    // Update cookie if Set-Cookie header is present
    const setCookie = response.headers['set-cookie'];
    if (setCookie && setCookie.length > 0) {
      this.logger.debug(`Updating cookie. user: ${exchangeDto.userName}`);
      await this.prisma.cookie.update({
        where: { id: exchangeDto.cookieId },
        data: { cookie: setCookie.join('; ') },
      });
    }

    const amount = this.extractAmount(response.data as string);

    // Save point record
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
   * Extract point amount from response body
   */
  private extractAmount(body: string): number {
    const match = body.match(this.amountPattern);
    if (match) {
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
   * Save call log
   */
  private async saveLog(pointUrl: PointUrl, exchangeDto: ExchangeDto, response: AxiosResponse): Promise<void> {
    // Save point URL cookie relationship
    await this.prisma.pointUrlCookie.create({
      data: {
        pointUrlId: pointUrl.id,
        cookieId: exchangeDto.cookieId,
      },
    });

    // Save call log
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

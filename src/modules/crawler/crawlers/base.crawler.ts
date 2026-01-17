import axios from 'axios';
import * as cheerio from 'cheerio';
import { Logger } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';

export interface SiteData {
  siteName: string;
  domain: string;
  boardUrl: string;
}

export interface CrawledUrl {
  url: string;
}

export abstract class BaseCrawler {
  protected readonly logger: Logger;
  protected readonly timeout: number;
  protected readonly retryCount: number;
  protected readonly userAgent: string;

  constructor(
    protected readonly configService: ConfigService,
    loggerContext: string,
  ) {
    this.logger = new Logger(loggerContext);
    this.timeout = this.configService.get<number>('crawler.timeout') || 10000;
    this.retryCount = this.configService.get<number>('crawler.retryCount') || 3;
    this.userAgent =
      this.configService.get<string>('naver.userAgent') ||
      'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36';
  }

  /**
   * Get site-specific data
   */
  abstract getSiteData(): SiteData;

  /**
   * Get CSS selector for article links
   */
  abstract getArticleSelector(): string;

  /**
   * Fetch post URLs from the site
   */
  abstract fetchPostUrls(siteUrl: string): Promise<Set<string>>;

  /**
   * Main crawling method
   */
  async crawl(siteUrl: string): Promise<CrawledUrl[]> {
    const siteData = this.getSiteData();
    this.logger.log(`Starting crawl for ${siteData.siteName}`);

    try {
      const postUrls = await this.fetchPostUrls(siteUrl);
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
   * Extract Naver point URLs from post URLs
   */
  protected async extractPointUrls(domain: string, postUrls: Set<string>): Promise<CrawledUrl[]> {
    const pointUrls: CrawledUrl[] = [];

    for (const url of postUrls) {
      try {
        const fullUrl = url.startsWith('http') ? url : `${domain}${url}`;
        const response = await axios.get(fullUrl, {
          timeout: this.timeout,
          headers: {
            'User-Agent': this.userAgent,
          },
        });

        const $ = cheerio.load(response.data);
        const selector = this.getArticleSelector();

        $(selector).each((_, element) => {
          const href = $(element).attr('href');
          if (href && this.isNaverPointUrl(href)) {
            this.logger.debug(`Found point URL: ${href}`);
            pointUrls.push({ url: href });
          }
        });
      } catch (error) {
        this.logger.warn(`Failed to crawl URL: ${domain}${url}. Error: ${(error as Error).message}`);
      }
    }

    return pointUrls;
  }

  /**
   * Check if URL is a Naver point URL
   */
  protected isNaverPointUrl(url: string): boolean {
    return (
      url != null &&
      (url.includes('naver.com/point') || url.includes('naver.me') || url.includes('m.site.naver.com'))
    );
  }

  /**
   * Fetch HTML content with retry
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
        if (attempt === retries) {
          throw error;
        }
        this.logger.warn(`Retry ${attempt}/${retries} for URL: ${url}`);
        await new Promise((resolve) => setTimeout(resolve, 1000 * attempt));
      }
    }
    throw new Error(`Failed to fetch after ${retries} retries`);
  }
}

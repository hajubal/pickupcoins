import { Injectable } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import * as cheerio from 'cheerio';
import { BaseCrawler, SiteData } from './base.crawler';

@Injectable()
export class ClienCrawler extends BaseCrawler {
  constructor(configService: ConfigService) {
    super(configService, ClienCrawler.name);
  }

  getSiteData(): SiteData {
    return {
      siteName: '클리앙',
      domain: 'https://www.clien.net',
      boardUrl: '/service/board/jirum',
    };
  }

  getArticleSelector(): string {
    return 'div.post_article a';
  }

  async fetchPostUrls(siteUrl: string): Promise<Set<string>> {
    const postUrls = new Set<string>();

    try {
      const html = await this.fetchWithRetry(siteUrl);
      const $ = cheerio.load(html);

      // Find all links that might contain point URLs
      // Clien uses 'span.list_subject' containing an <a> tag for post titles
      $('span.list_subject > a').each((_, element) => {
        const href = $(element).attr('href');
        if (href && href.includes('/service/board/jirum/')) {
          // Remove query params and anchors to get clean URL
          const cleanHref = href.split('?')[0].split('#')[0];
          postUrls.add(cleanHref);
        }
      });

      this.logger.debug(`Found ${postUrls.size} post URLs from Clien`);
    } catch (error) {
      this.logger.error(`Failed to fetch post URLs from Clien`, error);
    }

    return postUrls;
  }
}

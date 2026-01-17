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
      // Clien uses 'subject' class for post titles
      $('a.list_subject').each((_, element) => {
        const href = $(element).attr('href');
        if (href && href.includes('/service/board/jirum/')) {
          postUrls.add(href);
        }
      });

      // Also check for mobile view
      $('a.subject_fixed').each((_, element) => {
        const href = $(element).attr('href');
        if (href && href.includes('/service/board/jirum/')) {
          postUrls.add(href);
        }
      });

      this.logger.debug(`Found ${postUrls.size} post URLs from Clien`);
    } catch (error) {
      this.logger.error(`Failed to fetch post URLs from Clien`, error);
    }

    return postUrls;
  }
}

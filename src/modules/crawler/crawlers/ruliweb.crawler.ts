import { Injectable } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import * as cheerio from 'cheerio';
import { BaseCrawler, SiteData } from './base.crawler';

@Injectable()
export class RuliwebCrawler extends BaseCrawler {
  constructor(configService: ConfigService) {
    super(configService, RuliwebCrawler.name);
  }

  getSiteData(): SiteData {
    return {
      siteName: '루리웹',
      domain: 'https://m.ruliweb.com',
      boardUrl: '/ps/board/1020',
    };
  }

  getArticleSelector(): string {
    return '.board_main_view a';
  }

  async fetchPostUrls(siteUrl: string): Promise<Set<string>> {
    const postUrls = new Set<string>();

    try {
      const html = await this.fetchWithRetry(siteUrl);
      const $ = cheerio.load(html);

      // Find all post links on Ruliweb mobile
      $('a.subject_link, a.title_text').each((_, element) => {
        const href = $(element).attr('href');
        if (href && (href.includes('/ps/board/') || href.includes('/bbs/board/'))) {
          postUrls.add(href);
        }
      });

      // Also check for desktop view links
      $('a.deco').each((_, element) => {
        const href = $(element).attr('href');
        if (href && (href.includes('/ps/board/') || href.includes('/bbs/board/'))) {
          postUrls.add(href);
        }
      });

      this.logger.debug(`Found ${postUrls.size} post URLs from Ruliweb`);
    } catch (error) {
      this.logger.error(`Failed to fetch post URLs from Ruliweb`, error);
    }

    return postUrls;
  }
}

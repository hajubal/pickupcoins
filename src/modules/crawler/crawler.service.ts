import { Injectable, Logger } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import { ClienCrawler } from './crawlers/clien.crawler';
import { RuliwebCrawler } from './crawlers/ruliweb.crawler';
import { BaseCrawler, CrawledUrl } from './crawlers/base.crawler';
import { classifyUrlType } from '../admin/point-url/dto/point-url.dto';

@Injectable()
export class CrawlerService {
  private readonly logger = new Logger(CrawlerService.name);
  private readonly crawlers: BaseCrawler[];

  constructor(
    private readonly prisma: PrismaService,
    private readonly clienCrawler: ClienCrawler,
    private readonly ruliwebCrawler: RuliwebCrawler,
  ) {
    this.crawlers = [clienCrawler, ruliwebCrawler];
  }

  /**
   * Run all crawlers and save new point URLs
   */
  async savingPointUrl(): Promise<number> {
    const allUrls = await this.crawlAllSites();
    return this.saveNewUrls(allUrls);
  }

  /**
   * Crawl all registered sites
   */
  private async crawlAllSites(): Promise<CrawledUrl[]> {
    const allUrls: CrawledUrl[] = [];

    for (const crawler of this.crawlers) {
      try {
        const siteData = crawler.getSiteData();

        // Get site from database
        const site = await this.prisma.site.findFirst({
          where: { name: siteData.siteName },
        });

        if (!site) {
          this.logger.warn(`Site not found: ${siteData.siteName}`);
          continue;
        }

        const urls = await crawler.crawl(site.url);
        allUrls.push(...urls);
      } catch (error) {
        this.logger.error(`Crawling failed for crawler`, error);
      }
    }

    return allUrls;
  }

  /**
   * Save new URLs that don't exist in database
   */
  private async saveNewUrls(urls: CrawledUrl[]): Promise<number> {
    const uniqueUrls = [...new Set(urls.map((u) => u.url))];

    // Check existing URLs
    const existingUrls = await this.prisma.pointUrl.findMany({
      where: { url: { in: uniqueUrls } },
      select: { url: true },
    });
    const existingUrlSet = new Set(existingUrls.map((p) => p.url));

    // Filter new URLs
    const newUrls = uniqueUrls.filter((url) => !existingUrlSet.has(url));

    if (newUrls.length === 0) {
      this.logger.log('No new point URLs found');
      return 0;
    }

    // Prepare data for bulk insert
    const data = newUrls.map((url) => {
      const pointUrlType = classifyUrlType(url);
      return {
        url,
        name: pointUrlType,
        pointUrlType,
        permanent: false,
      };
    });

    // Bulk insert
    const result = await this.prisma.pointUrl.createMany({
      data,
      skipDuplicates: true,
    });

    this.logger.log(`Saved ${result.count} new point URLs`);
    return result.count;
  }

  /**
   * Trigger crawling for a specific site
   */
  async crawlSite(siteName: string): Promise<CrawledUrl[]> {
    const crawler = this.crawlers.find((c) => c.getSiteData().siteName === siteName);

    if (!crawler) {
      throw new Error(`Crawler not found for site: ${siteName}`);
    }

    const siteData = crawler.getSiteData();
    const site = await this.prisma.site.findFirst({
      where: { name: siteData.siteName },
    });

    if (!site) {
      throw new Error(`Site not found in database: ${siteName}`);
    }

    return crawler.crawl(site.url);
  }

  /**
   * Get list of available crawlers
   */
  getCrawlerInfo() {
    return this.crawlers.map((c) => c.getSiteData());
  }
}

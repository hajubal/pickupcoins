import { Module } from '@nestjs/common';
import { CrawlerController } from './crawler.controller';
import { CrawlerService } from './crawler.service';
import { ClienCrawler } from './crawlers/clien.crawler';
import { RuliwebCrawler } from './crawlers/ruliweb.crawler';

@Module({
  controllers: [CrawlerController],
  providers: [CrawlerService, ClienCrawler, RuliwebCrawler],
  exports: [CrawlerService],
})
export class CrawlerModule {}

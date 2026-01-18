import { Controller, Get, Post, Param } from '@nestjs/common';
import { ApiTags, ApiOperation, ApiResponse, ApiBearerAuth } from '@nestjs/swagger';
import { CrawlerService } from './crawler.service';
import { Public } from '../auth/public.decorator';

@ApiTags('Crawler')
@ApiBearerAuth('JWT-auth')
@Public() // TODO: Remove after testing
@Controller('crawler')
export class CrawlerController {
  constructor(private readonly crawlerService: CrawlerService) {}

  @Post('trigger')
  @ApiOperation({ summary: '크롤링 수동 실행' })
  @ApiResponse({ status: 200, description: '크롤링 결과' })
  async triggerCrawl(): Promise<{ savedCount: number; message: string }> {
    const savedCount = await this.crawlerService.savingPointUrl();
    return {
      savedCount,
      message: `Crawling completed. ${savedCount} new URLs saved.`,
    };
  }

  @Post('trigger/:siteName')
  @ApiOperation({ summary: '특정 사이트 크롤링 수동 실행' })
  @ApiResponse({ status: 200, description: '크롤링 결과' })
  async triggerSiteCrawl(@Param('siteName') siteName: string): Promise<{ urlCount: number; message: string }> {
    const urls = await this.crawlerService.crawlSite(siteName);
    return {
      urlCount: urls.length,
      message: `Crawling completed for ${siteName}. ${urls.length} URLs found.`,
    };
  }

  @Get('sites')
  @ApiOperation({ summary: '등록된 크롤러 목록' })
  @ApiResponse({ status: 200, description: '크롤러 목록' })
  async getCrawlerSites() {
    return this.crawlerService.getCrawlerInfo();
  }
}

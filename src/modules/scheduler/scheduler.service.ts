import { Injectable, Logger } from '@nestjs/common';
import { Cron, Interval } from '@nestjs/schedule';
import { ConfigService } from '@nestjs/config';
import { CrawlerService } from '../crawler/crawler.service';
import { PointService } from '../point/point.service';
import { ReportService } from './report.service';

@Injectable()
export class SchedulerService {
  private readonly logger = new Logger(SchedulerService.name);
  private readonly crawlerDelay: number;
  private readonly pointDelay: number;
  private readonly dailyReportCron: string;
  private readonly isEnabled: boolean;

  constructor(
    private readonly configService: ConfigService,
    private readonly crawlerService: CrawlerService,
    private readonly pointService: PointService,
    private readonly reportService: ReportService,
  ) {
    this.crawlerDelay = this.configService.get<number>('schedule.crawlerFixedDelay') || 300000;
    this.pointDelay = this.configService.get<number>('schedule.pointFixedDelay') || 300000;
    this.dailyReportCron = this.configService.get<string>('schedule.dailyReportCron') || '0 0 7 * * *';
    this.isEnabled = this.configService.get<string>('nodeEnv') === 'development' ||
                     this.configService.get<string>('nodeEnv') === 'production';

    this.logger.log(`Scheduler initialized - Enabled: ${this.isEnabled}`);
    this.logger.log(`Crawler delay: ${this.crawlerDelay}ms, Point delay: ${this.pointDelay}ms`);
    this.logger.log(`Daily report cron: ${this.dailyReportCron}`);
  }

  /**
   * Web crawler scheduler - runs every 5 minutes
   */
  @Interval(300000) // 5 minutes default, can be overridden
  async webCrawlerScheduler(): Promise<void> {
    if (!this.isEnabled) {
      return;
    }

    this.logger.debug('Starting web crawler scheduler');

    try {
      const count = await this.crawlerService.savingPointUrl();
      this.logger.log(`Web crawler scheduler completed. New URLs: ${count}`);
    } catch (error) {
      this.logger.error('Web crawler scheduler failed', error);
    }
  }

  /**
   * Point collection scheduler - runs every 5 minutes
   */
  @Interval(300000) // 5 minutes default
  async pointScheduler(): Promise<void> {
    if (!this.isEnabled) {
      return;
    }

    this.logger.debug('Starting point scheduler');

    try {
      const result = await this.pointService.savePoint();
      this.logger.log(`Point scheduler completed. Processed: ${result.processed}, Points: ${result.points}`);
    } catch (error) {
      this.logger.error('Point scheduler failed', error);
    }
  }

  /**
   * Daily report scheduler - runs at 7 AM daily
   */
  @Cron('0 0 7 * * *')
  async dailyReport(): Promise<void> {
    if (!this.isEnabled) {
      return;
    }

    this.logger.debug('Starting daily report');

    try {
      await this.reportService.report();
      this.logger.log('Daily report completed');
    } catch (error) {
      this.logger.error('Daily report failed', error);
    }
  }

  /**
   * Manual trigger for crawler
   */
  async triggerCrawler(): Promise<number> {
    this.logger.log('Manual crawler trigger');
    return this.crawlerService.savingPointUrl();
  }

  /**
   * Manual trigger for point collection
   */
  async triggerPointCollection(): Promise<{ processed: number; points: number; errors: number }> {
    this.logger.log('Manual point collection trigger');
    return this.pointService.savePoint();
  }

  /**
   * Manual trigger for daily report
   */
  async triggerDailyReport(): Promise<void> {
    this.logger.log('Manual daily report trigger');
    return this.reportService.report();
  }
}

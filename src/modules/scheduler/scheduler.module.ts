import { Module } from '@nestjs/common';
import { SchedulerService } from './scheduler.service';
import { ReportService } from './report.service';
import { CrawlerModule } from '../crawler/crawler.module';
import { PointModule } from '../point/point.module';
import { NotificationModule } from '../notification/notification.module';

@Module({
  imports: [CrawlerModule, PointModule, NotificationModule],
  providers: [SchedulerService, ReportService],
  exports: [SchedulerService, ReportService],
})
export class SchedulerModule {}

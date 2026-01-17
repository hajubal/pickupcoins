import { Module } from '@nestjs/common';
import { ConfigModule } from '@nestjs/config';
import { ScheduleModule } from '@nestjs/schedule';
import configuration from './config/configuration';
import { PrismaModule } from './modules/prisma/prisma.module';
import { AuthModule } from './modules/auth/auth.module';
import { CookieModule } from './modules/admin/cookie/cookie.module';
import { SiteModule } from './modules/admin/site/site.module';
import { PointUrlModule } from './modules/admin/point-url/point-url.module';
import { SavedPointModule } from './modules/admin/saved-point/saved-point.module';
import { DashboardModule } from './modules/admin/dashboard/dashboard.module';
import { CrawlerModule } from './modules/crawler/crawler.module';
import { PointModule } from './modules/point/point.module';
import { SchedulerModule } from './modules/scheduler/scheduler.module';
import { NotificationModule } from './modules/notification/notification.module';
import { HealthModule } from './health/health.module';

@Module({
  imports: [
    ConfigModule.forRoot({
      isGlobal: true,
      load: [configuration],
      envFilePath: ['.env', '.env.local'],
    }),
    ScheduleModule.forRoot(),
    PrismaModule,
    AuthModule,
    HealthModule,
    CookieModule,
    SiteModule,
    PointUrlModule,
    SavedPointModule,
    DashboardModule,
    CrawlerModule,
    PointModule,
    SchedulerModule,
    NotificationModule,
  ],
})
export class AppModule {}

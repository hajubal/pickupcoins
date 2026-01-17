import { Injectable, Logger } from '@nestjs/common';
import { PrismaService } from '../../prisma/prisma.service';
import { DashboardStatsDto } from './dto/dashboard.dto';

@Injectable()
export class DashboardService {
  private readonly logger = new Logger(DashboardService.name);

  constructor(private readonly prisma: PrismaService) {}

  async getStats(): Promise<DashboardStatsDto> {
    const now = new Date();
    const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());
    const tomorrow = new Date(today);
    tomorrow.setDate(tomorrow.getDate() + 1);

    // Yesterday range
    const yesterdayStart = new Date(today);
    yesterdayStart.setDate(yesterdayStart.getDate() - 1);
    const yesterdayEnd = new Date(today);

    // This week range (from Monday)
    const dayOfWeek = today.getDay();
    const mondayOffset = dayOfWeek === 0 ? -6 : 1 - dayOfWeek;
    const thisWeekStart = new Date(today);
    thisWeekStart.setDate(thisWeekStart.getDate() + mondayOffset);
    const thisWeekEnd = tomorrow;

    // Last week range
    const lastWeekStart = new Date(thisWeekStart);
    lastWeekStart.setDate(lastWeekStart.getDate() - 7);
    const lastWeekEnd = new Date(thisWeekStart);

    // Today's points
    const todayPointsResult = await this.prisma.savedPoint.aggregate({
      where: {
        createdDate: { gte: today, lt: tomorrow },
      },
      _sum: { amount: true },
    });
    const savedDayPoint = todayPointsResult._sum.amount || 0;

    // Yesterday's points
    const yesterdayPointsResult = await this.prisma.savedPoint.aggregate({
      where: {
        createdDate: { gte: yesterdayStart, lt: yesterdayEnd },
      },
      _sum: { amount: true },
    });
    const yesterdaySavedPoint = yesterdayPointsResult._sum.amount || 0;

    // Day-over-day ratio
    let savedDayPointRatioDayBefore = 0;
    if (yesterdaySavedPoint > 0) {
      savedDayPointRatioDayBefore = ((savedDayPoint - yesterdaySavedPoint) / yesterdaySavedPoint) * 100;
    } else if (savedDayPoint > 0) {
      savedDayPointRatioDayBefore = 100;
    }

    // This week's points
    const thisWeekPointsResult = await this.prisma.savedPoint.aggregate({
      where: {
        createdDate: { gte: thisWeekStart, lt: thisWeekEnd },
      },
      _sum: { amount: true },
    });
    const savedWeekPoint = thisWeekPointsResult._sum.amount || 0;

    // Last week's points
    const lastWeekPointsResult = await this.prisma.savedPoint.aggregate({
      where: {
        createdDate: { gte: lastWeekStart, lt: lastWeekEnd },
      },
      _sum: { amount: true },
    });
    const lastWeekSavedPoint = lastWeekPointsResult._sum.amount || 0;

    // Week-over-week ratio
    let savedWeekPointRatioWeekBefore = 0;
    if (lastWeekSavedPoint > 0) {
      savedWeekPointRatioWeekBefore = ((savedWeekPoint - lastWeekSavedPoint) / lastWeekSavedPoint) * 100;
    } else if (savedWeekPoint > 0) {
      savedWeekPointRatioWeekBefore = 100;
    }

    // Today's point URLs count
    const pointUrlDayCnt = await this.prisma.pointUrl.count({
      where: {
        createdDate: { gte: today, lt: tomorrow },
      },
    });

    // This week's point URLs count
    const pointUrlWeekCnt = await this.prisma.pointUrl.count({
      where: {
        createdDate: { gte: thisWeekStart, lt: thisWeekEnd },
      },
    });

    this.logger.log(
      `Dashboard stats - Today points: ${savedDayPoint}, Week points: ${savedWeekPoint}, Today URLs: ${pointUrlDayCnt}, Week URLs: ${pointUrlWeekCnt}`,
    );

    return {
      savedDayPoint,
      savedDayPointRatioDayBefore: Math.round(savedDayPointRatioDayBefore * 10) / 10,
      savedWeekPoint,
      savedWeekPointRatioWeekBefore: Math.round(savedWeekPointRatioWeekBefore * 10) / 10,
      pointUrlDayCnt,
      pointUrlWeekCnt,
    };
  }
}

import { Injectable, Logger } from '@nestjs/common';
import { PrismaService } from '../../prisma/prisma.service';
import { DashboardStatsDto } from './dto/dashboard.dto';

/**
 * 대시보드 통계 서비스
 *
 * 포인트 수집 현황을 분석하여 대시보드에 표시할 통계 데이터를 제공
 *
 * 제공 통계:
 * 1. 오늘 적립 포인트 및 전일 대비 증감률
 * 2. 금주 적립 포인트 및 전주 대비 증감률
 * 3. 오늘 수집된 URL 개수
 * 4. 금주 수집된 URL 개수
 *
 * 날짜 기준:
 * - 일간: 오늘 00:00 ~ 내일 00:00
 * - 주간: 이번 주 월요일 00:00 ~ 다음 주 월요일 00:00
 */
@Injectable()
export class DashboardService {
  private readonly logger = new Logger(DashboardService.name);

  constructor(private readonly prisma: PrismaService) {}

  /**
   * 대시보드 통계 조회
   *
   * @returns 대시보드 통계 DTO
   *
   * 계산 내용:
   * 1. savedDayPoint: 오늘 적립 포인트 합계
   * 2. savedDayPointRatioDayBefore: 전일 대비 증감률 (%)
   * 3. savedWeekPoint: 금주 적립 포인트 합계
   * 4. savedWeekPointRatioWeekBefore: 전주 대비 증감률 (%)
   * 5. pointUrlDayCnt: 오늘 수집된 URL 개수
   * 6. pointUrlWeekCnt: 금주 수집된 URL 개수
   */
  async getStats(): Promise<DashboardStatsDto> {
    // ===== 날짜 범위 계산 =====
    const now = new Date();

    // 오늘 00:00
    const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());

    // 내일 00:00 (오늘 범위의 끝)
    const tomorrow = new Date(today);
    tomorrow.setDate(tomorrow.getDate() + 1);

    // 어제 범위: 어제 00:00 ~ 오늘 00:00
    const yesterdayStart = new Date(today);
    yesterdayStart.setDate(yesterdayStart.getDate() - 1);
    const yesterdayEnd = new Date(today);

    // 이번 주 월요일 계산
    // getDay(): 0=일요일, 1=월요일, ..., 6=토요일
    const dayOfWeek = today.getDay();
    const mondayOffset = dayOfWeek === 0 ? -6 : 1 - dayOfWeek;  // 일요일이면 -6, 아니면 월요일까지 오프셋
    const thisWeekStart = new Date(today);
    thisWeekStart.setDate(thisWeekStart.getDate() + mondayOffset);
    const thisWeekEnd = tomorrow;  // 오늘까지 포함

    // 지난 주 범위: 지난 주 월요일 ~ 이번 주 월요일
    const lastWeekStart = new Date(thisWeekStart);
    lastWeekStart.setDate(lastWeekStart.getDate() - 7);
    const lastWeekEnd = new Date(thisWeekStart);

    // ===== 포인트 통계 조회 =====

    // 오늘 적립 포인트 합계
    const todayPointsResult = await this.prisma.savedPoint.aggregate({
      where: {
        createdDate: { gte: today, lt: tomorrow },
      },
      _sum: { amount: true },
    });
    const savedDayPoint = todayPointsResult._sum.amount || 0;

    // 어제 적립 포인트 합계
    const yesterdayPointsResult = await this.prisma.savedPoint.aggregate({
      where: {
        createdDate: { gte: yesterdayStart, lt: yesterdayEnd },
      },
      _sum: { amount: true },
    });
    const yesterdaySavedPoint = yesterdayPointsResult._sum.amount || 0;

    // 전일 대비 증감률 계산
    // - 어제 포인트 > 0: (오늘 - 어제) / 어제 * 100
    // - 어제 포인트 = 0 && 오늘 > 0: 100%
    // - 둘 다 0: 0%
    let savedDayPointRatioDayBefore = 0;
    if (yesterdaySavedPoint > 0) {
      savedDayPointRatioDayBefore = ((savedDayPoint - yesterdaySavedPoint) / yesterdaySavedPoint) * 100;
    } else if (savedDayPoint > 0) {
      savedDayPointRatioDayBefore = 100;
    }

    // 금주 적립 포인트 합계
    const thisWeekPointsResult = await this.prisma.savedPoint.aggregate({
      where: {
        createdDate: { gte: thisWeekStart, lt: thisWeekEnd },
      },
      _sum: { amount: true },
    });
    const savedWeekPoint = thisWeekPointsResult._sum.amount || 0;

    // 지난 주 적립 포인트 합계
    const lastWeekPointsResult = await this.prisma.savedPoint.aggregate({
      where: {
        createdDate: { gte: lastWeekStart, lt: lastWeekEnd },
      },
      _sum: { amount: true },
    });
    const lastWeekSavedPoint = lastWeekPointsResult._sum.amount || 0;

    // 전주 대비 증감률 계산 (전일 대비와 동일 로직)
    let savedWeekPointRatioWeekBefore = 0;
    if (lastWeekSavedPoint > 0) {
      savedWeekPointRatioWeekBefore = ((savedWeekPoint - lastWeekSavedPoint) / lastWeekSavedPoint) * 100;
    } else if (savedWeekPoint > 0) {
      savedWeekPointRatioWeekBefore = 100;
    }

    // ===== URL 통계 조회 =====

    // 오늘 수집된 URL 개수
    const pointUrlDayCnt = await this.prisma.pointUrl.count({
      where: {
        createdDate: { gte: today, lt: tomorrow },
      },
    });

    // 금주 수집된 URL 개수
    const pointUrlWeekCnt = await this.prisma.pointUrl.count({
      where: {
        createdDate: { gte: thisWeekStart, lt: thisWeekEnd },
      },
    });

    this.logger.log(
      `Dashboard stats - Today points: ${savedDayPoint}, Week points: ${savedWeekPoint}, Today URLs: ${pointUrlDayCnt}, Week URLs: ${pointUrlWeekCnt}`,
    );

    // ===== 결과 반환 =====
    return {
      savedDayPoint,
      savedDayPointRatioDayBefore: Math.round(savedDayPointRatioDayBefore * 10) / 10,   // 소수점 1자리
      savedWeekPoint,
      savedWeekPointRatioWeekBefore: Math.round(savedWeekPointRatioWeekBefore * 10) / 10, // 소수점 1자리
      pointUrlDayCnt,
      pointUrlWeekCnt,
    };
  }
}

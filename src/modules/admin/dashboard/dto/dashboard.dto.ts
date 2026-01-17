import { ApiProperty } from '@nestjs/swagger';

export class DashboardStatsDto {
  @ApiProperty({ description: '오늘 적립 포인트' })
  savedDayPoint: number;

  @ApiProperty({ description: '전일 대비 비율 (%)' })
  savedDayPointRatioDayBefore: number;

  @ApiProperty({ description: '이번 주 적립 포인트' })
  savedWeekPoint: number;

  @ApiProperty({ description: '전주 대비 비율 (%)' })
  savedWeekPointRatioWeekBefore: number;

  @ApiProperty({ description: '오늘 수집된 포인트 URL 수' })
  pointUrlDayCnt: number;

  @ApiProperty({ description: '이번 주 수집된 포인트 URL 수' })
  pointUrlWeekCnt: number;
}

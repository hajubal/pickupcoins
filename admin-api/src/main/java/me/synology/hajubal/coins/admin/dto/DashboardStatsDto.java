package me.synology.hajubal.coins.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDto {
  private Integer savedDayPoint;
  private Double savedDayPointRatioDayBefore;
  private Integer savedWeekPoint;
  private Double savedWeekPointRatioWeekBefore;
  private Long pointUrlDayCnt;
  private Long pointUrlWeekCnt;
}

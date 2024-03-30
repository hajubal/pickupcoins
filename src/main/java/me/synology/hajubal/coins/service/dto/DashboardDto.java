package me.synology.hajubal.coins.service.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class DashboardDto {

    private final int pointUrlDayCnt;

    private final int savedDayPoint;

    private final int pointUrlWeekCnt;

    private final int savedWeekPoint;

    private final int totalCookieCnt;

    private final int loginCookieCnt;
}

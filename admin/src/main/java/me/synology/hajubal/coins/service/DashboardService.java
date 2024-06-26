package me.synology.hajubal.coins.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.entity.Cookie;
import me.synology.hajubal.coins.entity.PointUrl;
import me.synology.hajubal.coins.entity.SavedPoint;
import me.synology.hajubal.coins.service.dto.DashboardDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
@Service
public class DashboardService {

    private final PointUrlService pointUrlService;

    private final SavedPointService savedPointService;

    private final CookieService cookieService;

    public DashboardDto getDashboard() {
        List<PointUrl> pointUrlDay = pointUrlService.findPointUrl(1);
        List<PointUrl> pointUrlWeek = pointUrlService.findPointUrl(7);

        List<SavedPoint> savedPointDay = savedPointService.findSavedPoint(1);
        List<SavedPoint> savedPoint2Day = savedPointService.findSavedPoint(2);
        List<SavedPoint> savedPointWeek = savedPointService.findSavedPoint(7);
        List<SavedPoint> savedPoint2Week = savedPointService.findSavedPoint(14);

        int dayPoint = savedPointDay.stream().mapToInt(SavedPoint::getAmount).sum();
        int day2Point = savedPoint2Day.stream().mapToInt(SavedPoint::getAmount).sum();
        int weekPoint = savedPointWeek.stream().mapToInt(SavedPoint::getAmount).sum();
        int week2Point = savedPoint2Week.stream().mapToInt(SavedPoint::getAmount).sum();

        double beforeDayPoint = day2Point - dayPoint; //2일 전 포인트
        double beforeWeekPoint = week2Point - weekPoint; //2주 전 포인트

        log.info("dayPoint: {}, beforeDayPoint: {}, weekPoint: {}, beforeWeekPoint: {}", dayPoint, beforeDayPoint, weekPoint, beforeWeekPoint);

        double dayPointRatioDayBefore = ((dayPoint - beforeDayPoint) / beforeDayPoint) * 100;
        double dayPointRatioWeekBefore = ((weekPoint - beforeWeekPoint) / beforeWeekPoint) * 100;

        //요일별 포인트 합산
        Map<Integer, List<SavedPoint>> dayGroup = new TreeMap<>(savedPointWeek.stream()
                .collect(Collectors.groupingBy(savedPoint -> savedPoint.getCreatedDate().getDayOfMonth())));

        List<Integer> points = dayGroup.values().stream().map(savedPoints -> savedPoints.stream()
                .mapToInt(SavedPoint::getAmount).sum()).toList();

        List<Cookie> cookies = cookieService.getAll();

        int count = cookies.stream().filter(Cookie::getIsValid).toList().size();

        return DashboardDto.builder()
                .loginCookieCnt(count)
                .totalCookieCnt(cookies.size())
                .pointUrlDayCnt(pointUrlDay.size())
                .pointUrlWeekCnt(pointUrlWeek.size())
                .savedDayPoint(dayPoint)
                .savedWeekPoint(weekPoint)
                .savedDayPointRatioDayBefore(Math.round(dayPointRatioDayBefore))
                .savedWeekPointRatioWeekBefore(Math.round(dayPointRatioWeekBefore))
                .points(points)
                .build();
    }

}


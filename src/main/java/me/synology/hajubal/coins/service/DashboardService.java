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
        List<SavedPoint> savedPointWeek = savedPointService.findSavedPoint(7);

        int dayPoint = savedPointDay.stream().mapToInt(SavedPoint::getAmount).sum();
        int weekPoint = savedPointWeek.stream().mapToInt(SavedPoint::getAmount).sum();

        List<Integer> points = savedPointWeek.stream().map(SavedPoint::getAmount).toList();

        List<Cookie> cookies = cookieService.getAll();

        int count = cookies.stream().filter(Cookie::getIsValid).toList().size();

        return DashboardDto.builder()
                .loginCookieCnt(count)
                .totalCookieCnt(cookies.size())
                .pointUrlDayCnt(pointUrlDay.size())
                .pointUrlWeekCnt(pointUrlWeek.size())
                .savedDayPoint(dayPoint)
                .savedWeekPoint(weekPoint)
                .points(points)
                .build();
    }
}


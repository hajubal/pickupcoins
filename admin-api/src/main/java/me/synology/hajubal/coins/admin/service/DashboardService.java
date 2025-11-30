package me.synology.hajubal.coins.admin.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.admin.dto.DashboardStatsDto;
import me.synology.hajubal.coins.entity.SavedPoint;
import me.synology.hajubal.coins.respository.PointUrlRepository;
import me.synology.hajubal.coins.respository.SavedPointRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

  private final SavedPointRepository savedPointRepository;
  private final PointUrlRepository pointUrlRepository;

  public DashboardStatsDto getStats() {
    // 오늘 날짜 범위
    LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
    LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

    // 어제 날짜 범위
    LocalDateTime yesterdayStart = LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.MIN);
    LocalDateTime yesterdayEnd = LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.MAX);

    // 이번 주 날짜 범위 (월요일부터)
    LocalDate today = LocalDate.now();
    LocalDate weekStart = today.minusDays(today.getDayOfWeek().getValue() - 1);
    LocalDateTime thisWeekStart = LocalDateTime.of(weekStart, LocalTime.MIN);
    LocalDateTime thisWeekEnd = todayEnd;

    // 지난 주 날짜 범위
    LocalDate lastWeekStart = weekStart.minusWeeks(1);
    LocalDate lastWeekEnd = weekStart.minusDays(1);
    LocalDateTime lastWeekStartTime = LocalDateTime.of(lastWeekStart, LocalTime.MIN);
    LocalDateTime lastWeekEndTime = LocalDateTime.of(lastWeekEnd, LocalTime.MAX);

    // 오늘 포인트 합계
    List<SavedPoint> todayPoints = savedPointRepository.findAllByCreatedDateBetween(todayStart, todayEnd);
    int savedDayPoint = todayPoints.stream().mapToInt(SavedPoint::getAmount).sum();

    // 어제 포인트 합계
    List<SavedPoint> yesterdayPoints =
        savedPointRepository.findAllByCreatedDateBetween(yesterdayStart, yesterdayEnd);
    int yesterdaySavedPoint = yesterdayPoints.stream().mapToInt(SavedPoint::getAmount).sum();

    // 전일 대비 비율 계산
    double savedDayPointRatioDayBefore = 0.0;
    if (yesterdaySavedPoint > 0) {
      savedDayPointRatioDayBefore =
          ((double) (savedDayPoint - yesterdaySavedPoint) / yesterdaySavedPoint) * 100;
    } else if (savedDayPoint > 0) {
      savedDayPointRatioDayBefore = 100.0;
    }

    // 이번 주 포인트 합계
    List<SavedPoint> thisWeekPoints =
        savedPointRepository.findAllByCreatedDateBetween(thisWeekStart, thisWeekEnd);
    int savedWeekPoint = thisWeekPoints.stream().mapToInt(SavedPoint::getAmount).sum();

    // 지난 주 포인트 합계
    List<SavedPoint> lastWeekPoints =
        savedPointRepository.findAllByCreatedDateBetween(lastWeekStartTime, lastWeekEndTime);
    int lastWeekSavedPoint = lastWeekPoints.stream().mapToInt(SavedPoint::getAmount).sum();

    // 전주 대비 비율 계산
    double savedWeekPointRatioWeekBefore = 0.0;
    if (lastWeekSavedPoint > 0) {
      savedWeekPointRatioWeekBefore =
          ((double) (savedWeekPoint - lastWeekSavedPoint) / lastWeekSavedPoint) * 100;
    } else if (savedWeekPoint > 0) {
      savedWeekPointRatioWeekBefore = 100.0;
    }

    // 오늘 생성된 PointUrl 수
    long pointUrlDayCnt = pointUrlRepository.findByCreatedDateBetween(todayStart, todayEnd).size();

    // 이번 주 생성된 PointUrl 수
    long pointUrlWeekCnt =
        pointUrlRepository.findByCreatedDateBetween(thisWeekStart, thisWeekEnd).size();

    log.info(
        "Dashboard stats - Today points: {}, Week points: {}, Today URLs: {}, Week URLs: {}",
        savedDayPoint,
        savedWeekPoint,
        pointUrlDayCnt,
        pointUrlWeekCnt);

    return DashboardStatsDto.builder()
        .savedDayPoint(savedDayPoint)
        .savedDayPointRatioDayBefore(Math.round(savedDayPointRatioDayBefore * 10.0) / 10.0)
        .savedWeekPoint(savedWeekPoint)
        .savedWeekPointRatioWeekBefore(Math.round(savedWeekPointRatioWeekBefore * 10.0) / 10.0)
        .pointUrlDayCnt(pointUrlDayCnt)
        .pointUrlWeekCnt(pointUrlWeekCnt)
        .build();
  }
}

package me.synology.hajubal.coins.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 스케줄 관련 설정 값
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "schedule")
public class ScheduleProperties {

  /**
   * 웹 크롤러 스케줄러 고정 지연 시간 (밀리초)
   */
  private long crawlerFixedDelay = 300000; // 5분

  /**
   * 포인트 적립 스케줄러 고정 지연 시간 (밀리초)
   */
  private long pointFixedDelay = 300000; // 5분

  /**
   * 일일 리포트 크론 표현식
   */
  private String dailyReportCron = "0 0 7 * * *"; // 매일 아침 7시
}

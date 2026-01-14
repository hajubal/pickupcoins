package me.synology.hajubal.coins.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.service.NaverSavePointService;
import me.synology.hajubal.coins.service.ReportService;
import me.synology.hajubal.coins.service.SlackService;
import me.synology.hajubal.coins.service.WebCrawlerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@EnableScheduling
@ConditionalOnProperty(name = "schedule.enabled", matchIfMissing = true)
@Component
public class Schedulers {

  private final NaverSavePointService naverSavePointService;
  private final WebCrawlerService webCrawlerService;
  private final ReportService reportService;
  private final SlackService slackService;

  @Value("${slack.system.webhook-url:}")
  private String systemWebhookUrl;

  /**
   * 웹 사이트 크롤링 스케줄러
   * 설정된 주기마다 웹 사이트를 크롤링하여 포인트 URL 수집
   */
  @Scheduled(fixedDelayString = "${schedule.crawler-fixed-delay}")
  public void webCrawlerScheduler() {
    log.debug("Starting web crawler scheduler");

    try {
      webCrawlerService.savingPointUrl();
      log.info("Web crawler scheduler completed");
    } catch (Exception e) {
      log.error("Web crawler scheduler failed", e);
      sendAlert("Web crawler scheduler failed: " + e.getMessage());
    }
  }

  /**
   * 포인트 적립 요청 스케줄러
   * 설정된 주기마다 수집된 포인트 URL로 적립 요청
   */
  @Scheduled(fixedDelayString = "${schedule.point-fixed-delay}")
  public void pointScheduler() {
    log.debug("Starting point scheduler");

    try {
      naverSavePointService.savePoint();
      log.info("Point scheduler completed");
    } catch (Exception e) {
      log.error("Point scheduler failed", e);
      sendAlert("Point scheduler failed: " + e.getMessage());
    }
  }

  /**
   * 일일 리포트 스케줄러
   * 매일 설정된 시간에 전날 작업 내용 리포트
   */
  @Scheduled(cron = "${schedule.daily-report-cron}")
  public void dailyReport() {
    log.debug("Starting daily report");

    try {
      reportService.report();
      log.info("Daily report completed");
    } catch (Exception e) {
      log.error("Daily report failed", e);
      sendAlert("Daily report failed: " + e.getMessage());
    }
  }

  private void sendAlert(String message) {
    if (systemWebhookUrl != null && !systemWebhookUrl.isEmpty()) {
      try {
        slackService.sendMessage(systemWebhookUrl, message);
      } catch (Exception e) {
        log.error("Failed to send system alert", e);
      }
    }
  }
}

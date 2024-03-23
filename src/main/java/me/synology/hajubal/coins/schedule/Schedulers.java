package me.synology.hajubal.coins.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.service.NaverSavePointService;
import me.synology.hajubal.coins.service.ReportService;
import me.synology.hajubal.coins.service.WebCrawlerService;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@EnableScheduling
@Component
public class Schedulers {

    private final NaverSavePointService naverSavePointService;

    private final WebCrawlerService webCrawlerService;

    private final ReportService reportService;


    /**
     * 5분 마다 웹 사이트 클롤링
     */
    @Scheduled(fixedDelay = 1000 * 60 * 5)
    public void webCrawlerScheduler() {
        log.info("Call webCrawlerScheduler.");

        webCrawlerService.crawling();
    }

    /**
     * 5분 마다 웹 사이트 포인트 적립 요청
     */
    @Profile("!local")
    @Scheduled(fixedDelay = 1000 * 60 * 5)
    public void pointScheduler() {
        log.info("Call pointScheduler.");

        naverSavePointService.savePoint();
    }

    /**
     * 매일 아침 9시에 어제 하루 동안 작업 알림
     */
    @Profile("!local")
    @Scheduled(cron = "0 0 9 * * *")
    public void dailyReport() {
        log.info("Daily report.");

        reportService.report();
    }
}

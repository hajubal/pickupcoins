package me.synology.hajubal.coins.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.service.NaverSavePointService;
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

    @Scheduled(fixedDelay = 1000 * 60 * 5)
    public void webCrawlerScheduler() {
        log.info("Call webCrawlerScheduler.");

        webCrawlerService.crawling();
    }

    @Profile("!local")
    @Scheduled(fixedDelay = 1000 * 60 * 5)
    public void pointScheduler() {
        log.info("Call pointScheduler.");

        naverSavePointService.savePoint();
    }
}

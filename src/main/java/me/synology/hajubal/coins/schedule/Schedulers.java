package me.synology.hajubal.coins.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.service.NaverPointService;
import me.synology.hajubal.coins.service.WebCrawlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@EnableScheduling
@Component
public class Schedulers {

    private final NaverPointService naverPointService;

    private final WebCrawlerService webCrawlerService;

    @Scheduled(fixedDelay = 1000 * 60 * 5)
    public void webCrawlerScheduler() {
        log.info("Call webCrawlerScheduler.");

        webCrawlerService.crawling();
    }

    @Scheduled(fixedDelay = 1000 * 60 * 5)
    public void pointScheduler() {
        log.info("Call pointScheduler.");

        naverPointService.savePoint();
    }
}

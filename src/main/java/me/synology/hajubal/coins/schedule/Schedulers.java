package me.synology.hajubal.coins.schedule;

import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.service.NaverPointService;
import me.synology.hajubal.coins.service.WebCrawler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@EnableAsync
@Component
public class Schedulers {

    @Autowired
    private WebCrawler webCrawler;

    @Autowired
    private NaverPointService naverPointService;

    @Scheduled(cron = "10 * * * * *")
    public void webCrawlerScheduler() {
        log.info("Call webCrawlerScheduler.");
        webCrawler.crawling();
    }

    @Scheduled(cron = "10 * * * * *")
    public void pointScheduler() {
        log.info("Call pointScheduler.");
        naverPointService.savePoint();
    }
}

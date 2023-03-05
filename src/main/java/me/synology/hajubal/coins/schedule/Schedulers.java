package me.synology.hajubal.coins.schedule;

import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.service.NaverPointService;
import me.synology.hajubal.coins.service.WebCrawlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@EnableScheduling
@Component
public class Schedulers {

    @Autowired
    private NaverPointService naverPointService;

    @Autowired
    private WebCrawlerService webCrawlerService;

    @Scheduled(cron = "0 */10 * * * *")
    public void webCrawlerScheduler() {
        log.info("Call webCrawlerScheduler.");

        webCrawlerService.crawling();
    }

    @Scheduled(cron = "0 */10 * * * *")
    public void pointScheduler() {
        log.info("Call pointScheduler.");

        naverPointService.savePoint();
    }
}

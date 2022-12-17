package me.synology.hajubal.coins.schedule;

import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.service.NaverPointService;
import me.synology.hajubal.coins.service.WebCrawler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@EnableScheduling
@Component
public class Schedulers {

    @Autowired
    private List<WebCrawler> webCrawlers;

    @Autowired
    private NaverPointService naverPointService;

    @Scheduled(cron = "0 */10 * * * *")
    public void webCrawlerScheduler() {
        log.info("Call webCrawlerScheduler.");

        webCrawlers.forEach(WebCrawler::crawling);
    }

    @Scheduled(cron = "0 */10 * * * *")
    public void pointScheduler() {
        log.info("Call pointScheduler.");

        naverPointService.savePoint();
    }
}

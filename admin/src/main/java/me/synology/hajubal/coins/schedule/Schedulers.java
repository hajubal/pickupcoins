package me.synology.hajubal.coins.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.service.WebCrawlerService;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@EnableScheduling
@Component
public class Schedulers {

    private final WebCrawlerService webCrawlerService;


    /**
     * 5분 마다 웹 사이트 클롤링
     */
    @Scheduled(fixedDelay = 1000 * 60 * 5)
    public void webCrawlerScheduler() {
        log.info("Call webCrawlerScheduler.");

        webCrawlerService.crawling();
    }

}

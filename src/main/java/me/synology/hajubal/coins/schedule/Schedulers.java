package me.synology.hajubal.coins.schedule;

import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.entity.PointUrl;
import me.synology.hajubal.coins.respository.PointUrlRepository;
import me.synology.hajubal.coins.service.NaverPointService;
import me.synology.hajubal.coins.crawler.WebCrawler;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private PointUrlRepository pointUrlRepository;

    @Scheduled(cron = "0 */10 * * * *")
    public void webCrawlerScheduler() {
        log.info("Call webCrawlerScheduler.");

        webCrawlers.forEach(webCrawlers -> webCrawlers.crawling().forEach(pointUrl -> {
                if(pointUrlRepository.findByUrl(pointUrl).isEmpty()) {
                    log.info("save point url: {}", pointUrl);

                    pointUrlRepository.save(PointUrl.builder()
                            .url(pointUrl)
                            .name(pointUrl)
                            .build());
                }
        }));
    }

    @Scheduled(cron = "0 */10 * * * *")
    public void pointScheduler() {
        log.info("Call pointScheduler.");

        naverPointService.savePoint();
    }
}

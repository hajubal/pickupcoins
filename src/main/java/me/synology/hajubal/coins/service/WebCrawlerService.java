package me.synology.hajubal.coins.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.crawler.WebCrawler;
import me.synology.hajubal.coins.entity.PointUrl;
import me.synology.hajubal.coins.respository.PointUrlRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Slf4j
@Service
public class WebCrawlerService {

    private final List<WebCrawler> webCrawlers;

    private final PointUrlRepository pointUrlRepository;

    public void crawling() {
        savePointUrls(crawlPointUrls());
    }

    private void savePointUrls(List<PointUrl> pointUrls) {
        pointUrls.forEach(pointUrl -> {
            if(pointUrlRepository.findByUrl(pointUrl.getUrl()).isEmpty()) {
                log.info("save point url: {}", pointUrl);

                pointUrlRepository.save(pointUrl);
            }
        });
    }

    private List<PointUrl> crawlPointUrls() {
        Set<PointUrl> pointUrlSet = new HashSet<>();

        webCrawlers.forEach(crawler -> {
            try {
                pointUrlSet.addAll(crawler.crawling());
            } catch (IOException e) {
                log.error("Crawling fail.");
                log.error(e.getMessage(), e);
            }
        });

        return new ArrayList<>(pointUrlSet);
    }
}

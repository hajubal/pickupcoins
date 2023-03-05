package me.synology.hajubal.coins.service;

import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.crawler.WebCrawler;
import me.synology.hajubal.coins.entity.PointUrl;
import me.synology.hajubal.coins.respository.PointUrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class WebCrawlerService {

    @Autowired
    private List<WebCrawler> webCrawlers;

    @Autowired
    private PointUrlRepository pointUrlRepository;

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

        webCrawlers.forEach(webCrawlers -> {
            try {
                pointUrlSet.addAll(webCrawlers.crawling());
            } catch (IOException e) {
                log.error("Crawling fail. site name: {}", webCrawlers.siteName());
                log.error(e.getMessage(), e);
            }
        });

        return new ArrayList<>(pointUrlSet);
    }
}

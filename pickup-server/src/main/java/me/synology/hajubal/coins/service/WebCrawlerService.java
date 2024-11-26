package me.synology.hajubal.coins.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.crawler.WebCrawler;
import me.synology.hajubal.coins.entity.PointUrl;
import me.synology.hajubal.coins.respository.PointUrlRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * crawling에 등록된 사이트에서 point url들을 수집하여 저장.
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class WebCrawlerService {

    private final List<WebCrawler> webCrawlers;

    private final PointUrlRepository pointUrlRepository;

    /**
     * 등록된 사이트 들에 save point url 저장
     */
    @Transactional
    public void savingPointUrl() {
        Set<PointUrl> pointUrls = crawlPointUrls();

        for (PointUrl pointUrl : pointUrls) {
            if(pointUrlRepository.findByUrl(pointUrl.getUrl()).isEmpty()) {
                log.info("save point url: {}", pointUrl);

                pointUrlRepository.save(pointUrl);
            }
        }
    }

    private Set<PointUrl> crawlPointUrls() {
        Set<PointUrl> pointUrlSet = new HashSet<>();

        for (WebCrawler crawler : webCrawlers) {
            try {
                pointUrlSet.addAll(crawler.crawling());
            } catch (IOException e) {
                log.error("Crawling fail.", e);
            }
        }

        return pointUrlSet;
    }
}

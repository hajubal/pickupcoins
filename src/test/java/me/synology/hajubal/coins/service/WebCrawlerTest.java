package me.synology.hajubal.coins.service;

import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.crawler.impl.ClienWebCrawler;
import me.synology.hajubal.coins.respository.PointUrlRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Transactional
@Slf4j
@SpringBootTest
class WebCrawlerTest {

    @Autowired
    private ClienWebCrawler webCrawler;

    @Autowired
    private PointUrlRepository pointUrlRepository;

    @Test
    void crawling() throws IOException {
        webCrawler.crawling();

        pointUrlRepository.findAll().forEach(pointUrl -> log.info("name:{}, url: {}", pointUrl.getName(), pointUrl.getUrl()));
    }
}

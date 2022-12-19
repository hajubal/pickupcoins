package me.synology.hajubal.coins.service;

import me.synology.hajubal.coins.crawler.impl.ClienWebCrawler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
class NaverPointServiceTest {

    @Autowired
    private NaverPointService naverPointService;

    @Autowired
    private ClienWebCrawler webCrawler;


    @Test
    void savePoint() {

        //webCrawler.crawling();

        //naverPointService.savePoint();
    }


}
package me.synology.hajubal.coins.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import static org.springframework.http.HttpMethod.GET;

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
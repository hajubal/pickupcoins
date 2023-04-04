package me.synology.hajubal.coins.service;

import me.synology.hajubal.coins.crawler.impl.clien.ClienWebCrawler;
import me.synology.hajubal.coins.entity.UserCookie;
import me.synology.hajubal.coins.respository.UserCookieRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class NaverPointServiceTest {

    @Autowired
    private NaverPointService naverPointService;

    @Autowired
    private ClienWebCrawler webCrawler;

    @Mock
    private UserCookieRepository userCookieRepository;

    @Test
    void savePoint() {

        //webCrawler.crawling();

        //naverPointService.savePoint();
    }


    @Test
    void exchange() {
    }

    @Test
    void savePointLog() {
        UserCookie userCookie = userCookieRepository.findAll().get(0);

        System.out.println("userCookie = " + userCookie);

    }
}
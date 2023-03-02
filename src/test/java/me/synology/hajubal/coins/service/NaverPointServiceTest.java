package me.synology.hajubal.coins.service;

import me.synology.hajubal.coins.crawler.impl.ClienWebCrawler;
import me.synology.hajubal.coins.entity.SavedPoint;
import me.synology.hajubal.coins.entity.UserCookie;
import me.synology.hajubal.coins.respository.UserCookieRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;


@SpringBootTest
class NaverPointServiceTest {

    @Autowired
    private NaverPointService naverPointService;

    @Autowired
    private ClienWebCrawler webCrawler;

    @Autowired
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

        SavedPoint savedPoint = naverPointService.savePointLog(userCookie);

        System.out.println("savedPoint = " + savedPoint);
    }
}
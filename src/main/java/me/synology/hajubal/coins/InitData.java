package me.synology.hajubal.coins;

import jakarta.annotation.PostConstruct;
import me.synology.hajubal.coins.entity.CookieData;
import me.synology.hajubal.coins.entity.SiteData;
import me.synology.hajubal.coins.entity.UrlData;
import me.synology.hajubal.coins.respository.CookieRepository;
import me.synology.hajubal.coins.respository.SiteRepository;
import me.synology.hajubal.coins.respository.UrlRepository;
import me.synology.hajubal.coins.respository.UrlRepositoryImpl;
import me.synology.hajubal.coins.service.NaverPointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InitData {

    @Autowired
    private NaverPointService naverPointService;

    @Autowired
    private CookieRepository cookieRepository;

    @Autowired
    private UrlRepository urlRepository;

    @Autowired
    private SiteRepository siteRepository;

    @PostConstruct
    public void init() {
        String COOKIE = "";

        CookieData cookieData = CookieData.builder().id(1l).cookie(COOKIE).build();

        cookieRepository.save(cookieData);

        siteRepository.save(SiteData.builder().id(1l).name("클리앙").url("https://www.clien.net/service/board/jirum").build());

        urlRepository.save(UrlData.builder().id(1l).url("https://campaign2-api.naver.com/click-point/?eventId=cr_2022120103_2212_1_1109").build());

        naverPointService.savePoint();
    }
}

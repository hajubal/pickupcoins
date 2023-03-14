package me.synology.hajubal.coins;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.conf.UserCookieProps;
import me.synology.hajubal.coins.crawler.WebCrawler;
import me.synology.hajubal.coins.entity.Site;
import me.synology.hajubal.coins.entity.UserCookie;
import me.synology.hajubal.coins.respository.SiteRepository;
import me.synology.hajubal.coins.respository.UserCookieRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class InitData implements ApplicationRunner {

    private final UserCookieRepository userCookieRepository;

    private final SiteRepository siteRepository;

    private final UserCookieProps userCookieProps;

    private final List<WebCrawler> webCrawlers;

    @Override
    public void run(ApplicationArguments args) {
        userCookieProps.getUserCookies().forEach(user -> {

            UserCookie userCookie = UserCookie.builder().userName(user.name()).siteName("naver").cookie(user.cookie()).isValid(true).build();

            if(userCookieRepository.findByCookie(userCookie.getCookie()).isEmpty()) {
                log.info("save cookie. name: {}", user.name());

                userCookieRepository.save(userCookie);
            }
        });

        if(siteRepository.findByName("클리앙").isEmpty()) {
            siteRepository.save(Site.builder().name("클리앙").domain("https://www.clien.net").url("https://www.clien.net/service/board/jirum").build());
        }

        if(siteRepository.findByName("루리웹").isEmpty()) {
            siteRepository.save(Site.builder().name("루리웹").domain("https://m.ruliweb.com").url("https://m.ruliweb.com/ps/board/1020").build());
        }

        webCrawlers.forEach(webCrawler -> {
            webCrawler.getDomain();
            webCrawler.getBoardUrls();

            siteRepository.findByUrl("");
        });
    }
}

package me.synology.hajubal.coins;

import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.conf.UserCookieProps;
import me.synology.hajubal.coins.entity.Site;
import me.synology.hajubal.coins.entity.UserCookie;
import me.synology.hajubal.coins.respository.SiteRepository;
import me.synology.hajubal.coins.respository.UserCookieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class InitData implements ApplicationRunner {

    @Autowired
    private UserCookieRepository userCookieRepository;

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private UserCookieProps userCookieProps;

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
    }
}

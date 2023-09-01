package me.synology.hajubal.coins;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.conf.UserCookieProps;
import me.synology.hajubal.coins.crawler.SiteData;
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
public class DataInitializer implements ApplicationRunner {

    private final UserCookieRepository userCookieRepository;

    private final SiteRepository siteRepository;

    private final UserCookieProps userCookieProps;

    private final List<SiteData> siteData;

    @Override
    public void run(ApplicationArguments args) {
        //2023.08.27 DB 업데이트 된 데이터 유실되므로 주석 처리
//        userCookieProps.getUserCookies().forEach(user -> {
//
//            UserCookie userCookie = UserCookie.builder().userName(user.name()).siteName("naver").cookie(user.cookie()).isValid(true).build();
//
//            if(userCookieRepository.findByCookie(userCookie.getCookie()).isEmpty()) {
//                log.info("save cookie. name: {}", user.name());
//
//                userCookieRepository.save(userCookie);
//            }
//        });

        siteData.forEach(data -> {
            if (siteRepository.findByName(data.getSiteName()).isEmpty()) {
                siteRepository.save(
                        Site.builder()
                        .name(data.getSiteName())
                        .domain(data.getDomain())
                        .url(data.getDomain() + data.getBoardUrl())
                        .build()
                );
            }
        });
    }
}

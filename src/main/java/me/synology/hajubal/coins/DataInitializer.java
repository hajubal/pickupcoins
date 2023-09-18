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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 사용자 쿠키 데이터.
 *
 * 데이터 저장소에 이미 동일한 사용자가 있으면 데이터를 업데이트 하지 않는다.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class DataInitializer implements ApplicationRunner {

    private final UserCookieRepository userCookieRepository;

    private final SiteRepository siteRepository;

    private final UserCookieProps userCookieProps;

    private final List<SiteData> siteData;

    @Transactional
    @Override
    public void run(ApplicationArguments args) {

        List<UserCookieProps.User> userCookies = userCookieProps.getUserCookies();

        log.info("Initialize user info.");

        /**
         * 2023.08.27 DB 업데이트 된 데이터 유실되므로 주석 처리
         * 2023.09.16 없는 사용자가 있는 경우 추가. 로컬 개발 환경에서 필요함.
         */
        userCookies.forEach(user -> {
            UserCookie userCookie = UserCookie.builder().userName(user.name()).siteName("naver").cookie(user.cookie()).isValid(true).build();

            log.info("User: {}", user);

            if(!userCookieRepository.existsByUserName(userCookie.getUserName())) {
                log.info("Save user cookie. {}", user.name());

                userCookieRepository.save(userCookie);
            } else {
                log.info("Exist user: {}", user.name());
            }
        });

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

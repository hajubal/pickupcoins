package me.synology.hajubal.coins;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.entity.UserCookie;
import me.synology.hajubal.coins.entity.Site;
import me.synology.hajubal.coins.respository.CookieRepository;
import me.synology.hajubal.coins.respository.SiteRepository;
import me.synology.hajubal.coins.respository.PointUrlRepository;
import me.synology.hajubal.coins.service.NaverPointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

@Profile({"!test"})
@Slf4j
@Component
public class InitData {

    @Autowired
    private CookieRepository cookieRepository;

    @Autowired
    private SiteRepository siteRepository;

    @PostConstruct
    public void init() throws IOException {
        String cookie = StreamUtils.copyToString(new FileInputStream("C:\\Users\\hajubal\\IdeaProjects\\pickupcoins\\tmp\\cookie.txt"), Charset.defaultCharset());

        System.out.println("cookie = " + cookie);

        UserCookie userCookie = UserCookie.builder().cookie(cookie).build();

        if(cookieRepository.findByCookie(cookie).isEmpty()) {
            cookieRepository.save(userCookie);
        }

        if(siteRepository.findByName("클리앙").isEmpty()) {
            siteRepository.save(Site.builder().name("클리앙").url("https://www.clien.net/service/board/jirum").build());
        }
    }
}

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

import java.io.FileReader;
import java.nio.CharBuffer;

@Profile({"!test"})
@Slf4j
@Component
public class InitData {

    @Autowired
    private CookieRepository cookieRepository;

    @Autowired
    private SiteRepository siteRepository;

    @PostConstruct
    public void init() {
        CharBuffer buffer = CharBuffer.allocate(10000);

        try (FileReader fileReader = new FileReader("C:\\Users\\hajubal\\IdeaProjects\\pickupcoins\\tmp\\cookie.txt")){
            fileReader.read(buffer);
        } catch (Exception e) {
            log.error(e.getMessage(), e);

            throw new RuntimeException(e);
        }

        String cookie = new String(buffer.compact().array());

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

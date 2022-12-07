package me.synology.hajubal.coins;

import jakarta.annotation.PostConstruct;
import me.synology.hajubal.coins.entity.CookieData;
import me.synology.hajubal.coins.entity.SiteData;
import me.synology.hajubal.coins.entity.PointUrlData;
import me.synology.hajubal.coins.respository.CookieRepository;
import me.synology.hajubal.coins.respository.SiteRepository;
import me.synology.hajubal.coins.respository.PointUrlRepository;
import me.synology.hajubal.coins.service.NaverPointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.FileReader;
import java.nio.CharBuffer;

@Component
public class InitData {

    @Autowired
    private NaverPointService naverPointService;

    @Autowired
    private CookieRepository cookieRepository;

    @Autowired
    private PointUrlRepository pointUrlRepository;

    @Autowired
    private SiteRepository siteRepository;

    @PostConstruct
    public void init() {
        CharBuffer buffer = CharBuffer.allocate(10000);


        try (FileReader fileReader = new FileReader("C:\\Users\\hajubal\\IdeaProjects\\pickupcoins\\tmp\\cookie.txt")){
            fileReader.read(buffer);

            System.out.println(">>>"+buffer);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String COOKIE = "";

        CookieData cookieData = CookieData.builder().id(1l).cookie(COOKIE).build();

        cookieRepository.save(cookieData);

        siteRepository.save(SiteData.builder().name("클리앙").url("https://www.clien.net/service/board/jirum").build());

        //pointUrlRepository.save(PointUrlData.builder().url("https://campaign2-api.naver.com/click-point/?eventId=cr_2022120103_2212_1_1109").build());

//        naverPointService.savePoint();
    }
}

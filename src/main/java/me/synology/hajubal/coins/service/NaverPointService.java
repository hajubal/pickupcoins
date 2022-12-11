package me.synology.hajubal.coins.service;

import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.entity.PointUrl;
import me.synology.hajubal.coins.entity.PointUrlCallHistory;
import me.synology.hajubal.coins.entity.UserCookie;
import me.synology.hajubal.coins.respository.PointUrlCallHistoryRepository;
import me.synology.hajubal.coins.respository.UserCookieRepository;
import me.synology.hajubal.coins.respository.PointUrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.springframework.http.HttpMethod.GET;

@Slf4j
@Transactional(readOnly = true)
@Service
public class NaverPointService {

    @Autowired
    private UserCookieRepository userCookieRepository;

    @Autowired
    private PointUrlRepository pointUrlRepository;

    @Autowired
    private PointUrlCallHistoryRepository pointUrlCallHistoryRepository;

    @Transactional
    public void savePoint() {
        HttpHeaders headers = new HttpHeaders();
        RestTemplate restTemplate = new RestTemplate();

        List<PointUrl> pointUrls = pointUrlRepository.findAll();

        log.info("pointUrls count: {}", pointUrls.size());

        pointUrls.forEach(url -> {
            List<UserCookie> userCookies = userCookieRepository.findBySiteName("naver");

            for (UserCookie userCookie: userCookies) {
                if(!pointUrlCallHistoryRepository.findByPointUrlAndUserName(url.getUrl(), userCookie.getUserName()).isEmpty()) continue;

                headers.clear();
                headers.add("Cookie", userCookie.getCookie());

                ResponseEntity<String> response = restTemplate.exchange(url.getUrl(), GET, new HttpEntity<String>(headers), String.class);

                log.debug("response: {} ", response);

                log.info("call point url. user name: {}", userCookie.getUserName());

                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                PointUrlCallHistory pointUrlCallHistory = PointUrlCallHistory.builder().pointUrl(url.getUrl())
                        .userName(userCookie.getUserName()).build();

                pointUrlCallHistoryRepository.save(pointUrlCallHistory);
            }
        });
    }
}

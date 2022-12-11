package me.synology.hajubal.coins.service;

import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.entity.PointUrl;
import me.synology.hajubal.coins.entity.UserCookie;
import me.synology.hajubal.coins.respository.CookieRepository;
import me.synology.hajubal.coins.respository.PointUrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
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
    private CookieRepository cookieRepository;

    @Autowired
    private PointUrlRepository pointUrlRepository;

    @Transactional
    public void savePoint() {
        HttpHeaders headers = new HttpHeaders();
        RestTemplate restTemplate = new RestTemplate();

        List<PointUrl> pointUrls = pointUrlRepository.findByCalledNull();

        log.info("pointUrls count: {}", pointUrls.size());

        pointUrls.forEach(url -> {
            UserCookie userCookie = cookieRepository.findBySiteName("naver").orElseThrow(() -> new IllegalArgumentException("Naver site name not found."));

            headers.clear();
            headers.add("Cookie", userCookie.getCookie());

            ResponseEntity<String> response = restTemplate.exchange(url.getUrl(), GET, new HttpEntity<String>(headers), String.class);

            url.setCalled(Boolean.TRUE);

            log.debug("response: {} ", response);
        });
    }
}

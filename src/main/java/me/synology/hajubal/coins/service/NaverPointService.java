package me.synology.hajubal.coins.service;

import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.entity.CookieData;
import me.synology.hajubal.coins.respository.CookieRepository;
import me.synology.hajubal.coins.respository.UrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static org.springframework.http.HttpMethod.GET;

@Slf4j
@Service
public class NaverPointService {

    @Autowired
    private CookieRepository cookieRepository;

    @Autowired
    private UrlRepository urlRepository;

    public void savePoint() {
        HttpHeaders headers = new HttpHeaders();
        RestTemplate restTemplate = new RestTemplate();

        urlRepository.findAll().forEach(url -> {
            cookieRepository.findAll().forEach(data -> {
                headers.clear();
                headers.add("Cookie", data.getCookie());

                ResponseEntity<String> response = restTemplate.exchange(url.getUrl(), GET, new HttpEntity<String>(headers), String.class);

                System.out.println("response.getBody() = " + response.getBody());
            });
        });


    }
}
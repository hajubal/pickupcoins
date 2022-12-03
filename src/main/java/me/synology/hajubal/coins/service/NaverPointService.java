package me.synology.hajubal.coins.service;

import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.entity.CookieData;
import me.synology.hajubal.coins.respository.CookieRepository;
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

    public static final String URL = "https://campaign2-api.naver.com/click-point/?eventId=cr_2022120103_2212_1_1109";

    @Autowired
    private CookieRepository cookieRepository;

    public void savePoint() {


        cookieRepository.findAll().forEach(data -> {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.add("Cookie", data.getCookie());

            ResponseEntity<String> response = restTemplate.exchange(URL, GET, new HttpEntity<String>(headers), String.class);

            System.out.println("response.getBody() = " + response.getBody());
        });
    }
}

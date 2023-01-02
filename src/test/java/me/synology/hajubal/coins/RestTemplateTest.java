package me.synology.hajubal.coins;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.springframework.http.HttpMethod.GET;

public class RestTemplateTest {

    //@Test
    void restTemplateTest() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("user-agent", "/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36");

        ResponseEntity<String> response = restTemplate.exchange("https://www.naver.com", GET, new HttpEntity<String>(headers), String.class);

        System.out.println("response = " + response.getHeaders());
    }
}

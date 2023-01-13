package me.synology.hajubal.coins.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import static org.springframework.http.HttpMethod.GET;

@Slf4j
//@SpringBootTest
public class PointServiceTest {
    @Test
    public void savePoint() {
        HttpHeaders headers = new HttpHeaders();
        RestTemplate restTemplate = new RestTemplate();

        //https://new-m.pay.naver.com/api/adreward/token?deviceType=pc&adId=298919&target=ad_detail&from=ad_detail

        headers.clear();
        headers.add("Cookie", "NID_AUT=8JnE9R49w0P++HMo866cr/gQQYfCz1buNwXLS/vact0; NID_SES=+IrVdotqwMinuAdueSWlTO3IJBh3c6aZcseZwpK379EdilasOqq54H3fJWodCeU0itdDefU8/vL2jrtpbWaVW8znAiqA8cQjOS1GD5bmoL0KJxERkcqot2qnpBSSaGIMOXpR3NAS4HTPvqlSTsQBP9Nj+JnBcG8XCXO3s+sqBKx1w/Hi5XNOyft7xR1nfo+1hHbbyQmIhBq14/gFz9Pdhtww6JLG3U5ml7xVwL5EPx3G9JwrQeRXMIrB0Bnn4b3XwV0He1xBa82PIyUhwM0noa0bMJzX1kDyUNHi7cj0IPKfkYjbLR770n0TMXfAVBevhTMFDS1c6d1zEYHukJxAiGFalu/hXE8fXVHRro+abn9kXVc5f/bV35HuU2IHG9eNqhJxrtEWUeXu6CF3MQRVm1+QQZq7eVRd8PMrCEW3VCnkVbjourgnbvdCmRfCQ+Am+xJ1wu034EAeEHFAcXwWT7o0uhvN4tvGJk9KBwSeYqXIU967yVfwTWXHgMe5TBL+36Rz+JPM2hSzKEugBKdACzy5MX2peteuzyuY2w1iYQ==");
        headers.add("user-agent", "/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36");

        ResponseEntity<String> response = restTemplate.exchange("https://new-m.pay.naver.com/api/adreward/token?deviceType=pc&adId=298919&target=ad_detail&from=ad_detail", GET, new HttpEntity<String>(headers), String.class);

        System.out.println("response = " + response);
        //response = <200 OK OK,{"code":"00","message":"성공","result":{"token":"eyJhbGciOiJIUzI1NiJ9.eyJ0eXBlIjoidmlldyIsInVpZCI6IjNBU01vOUdSMVVoWjY0eHlaT2hZWDNjM0Y5ZURCNnV6QXd0M1NwWm9NTmc9IiwicHVibGlzaGVyX2FwcF9pZCI6NjYsInRpbWVzdGFtcCI6MTY3MTcyMzI2MTQ1OH0.y7h41UaDVQ-P-i2f4Y_VYwob1cpUCl6vbudFaYzazEM","viewUrl":"https://ofw.adison.co/u/naverpay/ads/298919?token=eyJhbGciOiJIUzI1NiJ9.eyJ0eXBlIjoidmlldyIsInVpZCI6IjNBU01vOUdSMVVoWjY0eHlaT2hZWDNjM0Y5ZURCNnV6QXd0M1NwWm9NTmc9IiwicHVibGlzaGVyX2FwcF9pZCI6NjYsInRpbWVzdGFtcCI6MTY3MTcyMzI2MTQ1OH0.y7h41UaDVQ-P-i2f4Y_VYwob1cpUCl6vbudFaYzazEM&from=ad_detail"}},[date:"Thu, 22 Dec 2022 15:34:21 GMT", content-type:"application/json; charset=utf-8", content-length:"572", vary:"Accept-Encoding", x-dns-prefetch-control:"off", x-frame-options:"SAMEORIGIN", strict-transport-security:"max-age=15552000; includeSubDomains", x-download-options:"noopen", x-content-type-options:"nosniff", x-xss-protection:"1; mode=block", x-naver-fin-request-id:"1b1ad610-820e-11ed-b4a5-01f2dddf4c42", expires:"Thu, 22 Dec 2022 15:34:20 GMT", cache-control:"no-cache", "no-store, no-cache, must-revalidate, post-check=0, pre-check=0", referrer-policy:"unsafe-url", server:"nfront"]>

    }

    //@Test
    void test() {
        String url = "https://ofw.adison.co/u/naverpay/ads/298919?token=eyJhbGciOiJIUzI1NiJ9.eyJ0eXBlIjoidmlldyIsInVpZCI6IjNBU01vOUdSMVVoWjY0eHlaT2hZWDNjM0Y5ZURCNnV6QXd0M1NwWm9NTmc9IiwicHVibGlzaGVyX2FwcF9pZCI6NjYsInRpbWVzdGFtcCI6MTY3MTcyMzI2MTQ1OH0.y7h41UaDVQ-P-i2f4Y_VYwob1cpUCl6vbudFaYzazEM&from=ad_detail";

        HttpHeaders headers = new HttpHeaders();
        RestTemplate restTemplate = new RestTemplate();

        //https://new-m.pay.naver.com/api/adreward/token?deviceType=pc&adId=298919&target=ad_detail&from=ad_detail

        headers.clear();
        headers.add("Cookie", "NID_AUT=8JnE9R49w0P++HMo866cr/gQQYfCz1buNwXLS/vact0; NID_SES=+IrVdotqwMinuAdueSWlTO3IJBh3c6aZcseZwpK379EdilasOqq54H3fJWodCeU0itdDefU8/vL2jrtpbWaVW8znAiqA8cQjOS1GD5bmoL0KJxERkcqot2qnpBSSaGIMOXpR3NAS4HTPvqlSTsQBP9Nj+JnBcG8XCXO3s+sqBKx1w/Hi5XNOyft7xR1nfo+1hHbbyQmIhBq14/gFz9Pdhtww6JLG3U5ml7xVwL5EPx3G9JwrQeRXMIrB0Bnn4b3XwV0He1xBa82PIyUhwM0noa0bMJzX1kDyUNHi7cj0IPKfkYjbLR770n0TMXfAVBevhTMFDS1c6d1zEYHukJxAiGFalu/hXE8fXVHRro+abn9kXVc5f/bV35HuU2IHG9eNqhJxrtEWUeXu6CF3MQRVm1+QQZq7eVRd8PMrCEW3VCnkVbjourgnbvdCmRfCQ+Am+xJ1wu034EAeEHFAcXwWT7o0uhvN4tvGJk9KBwSeYqXIU967yVfwTWXHgMe5TBL+36Rz+JPM2hSzKEugBKdACzy5MX2peteuzyuY2w1iYQ==");
        headers.add("user-agent", "/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36");

        ResponseEntity<String> response = restTemplate.exchange(url, GET, new HttpEntity<String>(headers), String.class);

        System.out.println("response = " + response);
    }

    @Test
    void test1() {
        /**
         *
         * 1.
         * https://ofw.adison.co/u/naverpay/ads/298915
         *
         * 	response:
         * 		set-cookie: _session_id=dc9e3b3e722eda53941f629eb337ed88; path=/; expires=Sun, 25 Dec 2022 15:07:33 -0000; HttpOnly
         */

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", "NID_AUT=8JnE9R49w0P++HMo866cr/gQQYfCz1buNwXLS/vact0; NID_SES=+IrVdotqwMinuAdueSWlTO3IJBh3c6aZcseZwpK379EdilasOqq54H3fJWodCeU0itdDefU8/vL2jrtpbWaVW8znAiqA8cQjOS1GD5bmoL0KJxERkcqot2qnpBSSaGIMOXpR3NAS4HTPvqlSTsQBP9Nj+JnBcG8XCXO3s+sqBKx1w/Hi5XNOyft7xR1nfo+1hHbbyQmIhBq14/gFz9Pdhtww6JLG3U5ml7xVwL5EPx3G9JwrQeRXMIrB0Bnn4b3XwV0He1xBa82PIyUhwM0noa0bMJzX1kDyUNHi7cj0IPKfkYjbLR770n0TMXfAVBevhTMFDS1c6d1zEYHukJxAiGFalu/hXE8fXVHRro+abn9kXVc5f/bV35HuU2IHG9eNqhJxrtEWUeXu6CF3MQRVm1+QQZq7eVRd8PMrCEW3VCnkVbjourgnbvdCmRfCQ+Am+xJ1wu034EAeEHFAcXwWT7o0uhvN4tvGJk9KBwSeYqXIU967yVfwTWXHgMe5TBL+36Rz+JPM2hSzKEugBKdACzy5MX2peteuzyuY2w1iYQ==");
        headers.add("user-agent", "/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36");

        ResponseEntity<String> exchange = restTemplate.exchange("https://ofw.adison.co/u/naverpay/ads/298915", GET, new HttpEntity<>(headers), String.class);

        log.info("Headers");
        exchange.getHeaders().forEach((key, value) -> log.info("Key: {}, value: {}", key, value));
        log.info("Body: {}", exchange.getBody());

        List<String> cookies = exchange.getHeaders().get("Set-Cookie");

        AtomicReference<String> sessionId = new AtomicReference<>();

        assert cookies != null;

        cookies.forEach(cookie -> Arrays.stream(cookie.split(";")).forEach(str -> {
            String[] cookieKeyValue = str.trim().split("=");

            if("_session_id".equals(cookieKeyValue[0])) {
                sessionId.set(cookieKeyValue[1]);
            }
        }));

        Optional<Stream<String>> first = cookies.stream().filter(s -> s.contains("_session_id"))
                .map(s -> Arrays.stream(s.split(";")).filter(s1 -> s1.contains("_session_id")).map(s1 -> s1.split("=")[0])).findFirst();

        String session = cookies.stream().filter(s -> s.contains("_session_id")).findFirst().map(s -> Arrays.stream(s.split(";")).filter(s1 -> s1.contains("_session_id")).findFirst().get().split("=")[0]).get();

        log.info("session: {}", session);

        //cookies.stream().filter(s -> Arrays.stream(s.split(";")).filter(s1 -> s1.contains("_session_id"))).findFirst();
//        String sessionId = Arrays.stream(cookies.split(";")).filter(s -> s.trim().contains("_session_id")).findFirst().orElseThrow(() -> new IllegalArgumentException("Not contain \"_session_id\""));
//
//        log.info("sessionId: {}", sessionId);
    }
}

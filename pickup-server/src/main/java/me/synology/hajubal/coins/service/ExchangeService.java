package me.synology.hajubal.coins.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.entity.*;
import me.synology.hajubal.coins.respository.PointUrlCallLogRepository;
import me.synology.hajubal.coins.respository.PointUrlCookieRepository;
import me.synology.hajubal.coins.service.dto.ExchangeDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;

/**
 * 포인트를 제공하느 url을 호출하는 서비스
 */
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
@Service
public class ExchangeService {
    private final PointUrlCookieRepository pointUrlCookieRepository;

    private final PointUrlCallLogRepository pointUrlCallLogRepository;

    private final PointManageService pointManageService;

    private final WebClient.Builder webClientBuilder;

    private final CookieService cookieService;

    /**
     * point url을 호출.
     *
     * @param url
     * @param exchangeDto
     */
    @Transactional
    public void exchange(PointUrl url, ExchangeDto exchangeDto) {
        log.info("Call point url: {}. user name: {}", url.getUrl(), exchangeDto.userName());

        WebClient webClient = webClientBuilder.build();

        Mono<ResponseEntity<String>> mono = webClient
                .get()
                .uri(URI.create(url.getUrl()))
                .headers(httpHeaders -> setCookieHeaders(httpHeaders, exchangeDto.cookie()))
                .retrieve()
                .toEntity(String.class)
                ;

        //호출 결과 처리
        mono.subscribe(response -> {
            if(response == null || !StringUtils.hasText(response.getBody())) {
                log.info("Exchange response is empty.");
                return;
            }

            String body = response.getBody();

            if(isInvalidCookie(body)) {
                cookieService.invalid(exchangeDto.cookieId(), exchangeDto.webHookUrl());
            } else if(isSavePoint(body)) {
                pointManageService.savePointPostProcess(exchangeDto, response);
            }

            log.debug("Response body: {} ", response.getBody());

            saveLog(url, exchangeDto.cookieId(), response);
        });
    }

    private static boolean isSavePoint(String content) {
        return content.contains("적립");
    }

    private static boolean isInvalidCookie(String content) {
        return content.contains("로그인이 필요");
    }

    private void setCookieHeaders(HttpHeaders headers, String userCookie) {
        headers.add("Cookie", userCookie);
        headers.add("user-agent", "/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36");
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveLog(PointUrl url, Long cookieId, ResponseEntity<String> response) {
        Cookie cookie = cookieService.getCookie(cookieId);

        //사용자 별 호출 url 정보 저장
        pointUrlCookieRepository.save(PointUrlCookie.builder()
                .pointUrl(url)
                .cookie(cookie)
                .build());

        //호출 log
        pointUrlCallLogRepository.save(PointUrlCallLog.builder()
                .cookie(cookie.getCookie())
                .responseBody(response.getBody())
                .responseHeader(response.getHeaders().toString())
                .responseStatusCode(response.getStatusCode().value())
                .siteName(cookie.getSiteName())
                .userName(cookie.getUserName())
                .cookie(cookie.getCookie())
                .pointUrl(url.getUrl())
                .build()
        );
    }

}

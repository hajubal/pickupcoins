package me.synology.hajubal.coins.service;

import com.slack.api.webhook.WebhookResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.entity.*;
import me.synology.hajubal.coins.respository.PointUrlCallLogRepository;
import me.synology.hajubal.coins.respository.PointUrlUserCookieRepository;
import me.synology.hajubal.coins.respository.SavedPointRepository;
import me.synology.hajubal.coins.service.dto.ExchangeDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
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
    private final SlackService slackService;

    private final PointUrlUserCookieRepository pointUrlUserCookieRepository;

    private final PointUrlCallLogRepository pointUrlCallLogRepository;

    private final SavedPointRepository savedPointRepository;

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
        log.info("Call point url: {}. user name: {}", url.getUrl(), exchangeDto.getUserName());

        WebClient webClient = webClientBuilder.build();

        Mono<ResponseEntity<String>> mono = webClient
                .get()
                .uri(URI.create(url.getUrl()))
                .headers(httpHeaders -> setCookieHeaders(httpHeaders, exchangeDto.getCookie()))
                .retrieve()
                .toEntity(String.class)
                ;

        //호출 결과 처리
        mono.subscribe(response -> {
            if(response == null || response.getBody() == null) {
                log.info("Exchange response is empty.");
                return;
            }

            String body = response.getBody();

            if(isInvalidCookie(body)) {
                cookieService.invalid(exchangeDto.getCookieId(), exchangeDto.getWebHookUrl());
            } else if(isSavePoint(body)) {
                savePointPostProcess(exchangeDto, response);
            }

            log.debug("Response body: {} ", response.getBody());

            //TODO 리팩토링 필요
            Cookie cookie = cookieService.getCookie(exchangeDto.getCookieId());

            saveLog(url, cookie, response);
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
    public void saveLog(PointUrl url, Cookie cookie, ResponseEntity<String> response) {
        //사용자 별 호출 url 정보 저장
        pointUrlUserCookieRepository.save(PointUrlCookie.builder()
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

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void savePointPostProcess(ExchangeDto exchangeDto, ResponseEntity<String> response) {
        //cookie session값 갱신
        if(response.getHeaders().containsKey("cookie")) {
            log.info("cookie 갱신 user: {}", exchangeDto.getUserName());
            cookieService.updateCookie(exchangeDto.getCookieId(), response.getHeaders().getFirst("cookie"));
        }

        //TODO 리팩토링 필요
        Cookie cookie = cookieService.getCookie(exchangeDto.getCookieId());

        //getBody() 는 "10원이 적립 되었습니다." 라는 문자열을 포함하고 있음.
        savedPointRepository.save(SavedPoint.builder()
                .point("코드 수정 필요")
                .cookie(cookie)
                .responseBody(response.getBody())
                .build());
    }
}

package me.synology.hajubal.coins.service;

import com.slack.api.webhook.WebhookResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.entity.*;
import me.synology.hajubal.coins.respository.PointUrlCallLogRepository;
import me.synology.hajubal.coins.respository.PointUrlUserCookieRepository;
import me.synology.hajubal.coins.respository.SavedPointRepository;
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

    @Transactional
    public void exchange(PointUrl url, Cookie cookie) {
        log.info("Call point url: {}. user name: {}", url.getUrl(), cookie.getUserName());

        WebClient webClient = webClientBuilder.build();

        Mono<ResponseEntity<String>> entityMono = webClient
                .get()
                .uri(URI.create(url.getUrl()))
                .headers(httpHeaders -> setCookieHeaders(httpHeaders, cookie.getCookie()))
                .retrieve()
                .toEntity(String.class)
                ;

        //호출 결과 처리
        entityMono.subscribe(response -> {
            if(response == null || response.getBody() == null) {
                return;
            }

            if(isNeedLogin(response)) {
                cookie.invalid();

                log.info("로그인이 풀린 사용자: {}, 사이트: {}, cookie: {}", cookie.getUserName(), cookie.getSiteName(), cookie.getCookie());

                WebhookResponse webhookResponse = slackService.sendMessage(cookie.getSiteUser().getSlackWebhookUrl(), "[ " + cookie.getUserName() + " ] 로그인 풀림.");

                log.info("Webhook response code: {}" , webhookResponse.getCode());
            } else if(isSavePoint(response)) {
                savePointPostProcess(cookie, response);
            }

            log.debug("Response body: {} ", response.getBody());

            saveLog(url, cookie, response);
        });
    }

    private static boolean isSavePoint(ResponseEntity<String> response) {
        return response.getBody().contains("적립");
    }

    private static boolean isNeedLogin(ResponseEntity<String> response) {
        return response.getBody().contains("로그인이 필요");
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
    public void savePointPostProcess(Cookie cookie, ResponseEntity<String> response) {
        //cookie session값 갱신
        if(response.getHeaders().containsKey("cookie")) {
            log.info("cookie 갱신 user: {}", cookie.getUserName());
            cookie.updateCookie(response.getHeaders().getFirst("cookie"));
        }

        //getBody() 는 "10원이 적립 되었습니다." 라는 문자열을 포함하고 있음.
        savedPointRepository.save(SavedPoint.builder()
                .point("코드 수정 필요")
                .cookie(cookie)
                .responseBody(response.getBody())
                .build());
    }
}

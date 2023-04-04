package me.synology.hajubal.coins.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.entity.*;
import me.synology.hajubal.coins.respository.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;

/**
 * naver point url에 접속하여 point를 적립하는 로직
 */
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
@Service
public class NaverPointService {

    private final UserCookieRepository userCookieRepository;

    private final PointUrlRepository pointUrlRepository;

    private final ExchangeService exchangeService;

    /**
     * 포인트 저장 로직
     */
    public void savePoint() {
        String urlName = "naver";

        List<UserCookie> userCookies = userCookieRepository.findBySiteNameAndIsValid(urlName, true);

        log.debug("UserCookies: {}", userCookies);

        userCookies.forEach(userCookie -> {
            List<PointUrl> pointUrls = pointUrlRepository.findByNotCalledUrl(urlName, userCookie.getUserName());

            pointUrls.forEach(pointUrl -> exchangeService.exchange(pointUrl, userCookie));
        });
    }

    @RequiredArgsConstructor
    @Slf4j
    @Transactional(readOnly = true)
    @Service
    public static class ExchangeService {
        private final SlackService slackService;

        private final PointUrlUserCookieRepository pointUrlUserCookieRepository;

        private final PointUrlCallLogRepository pointUrlCallLogRepository;

        private final SavedPointRepository savedPointRepository;

        @Transactional
        public void exchange(PointUrl url, UserCookie userCookie) {
            log.info("Call point url: {}. user name: {}", url.getUrl(), userCookie.getUserName());

            WebClient webClient = WebClient.create();

            Mono<ResponseEntity<String>> entityMono = webClient
                    .get()
                    .uri(URI.create(url.getUrl()))
                    .headers(httpHeaders -> setCookieHeaders(httpHeaders, userCookie.getCookie()))
                    .retrieve()
                    .toEntity(String.class)
                    ;

            entityMono.subscribe(response -> {
                if(response == null || response.getBody() == null) {
                    return;
                }

                if(response.getBody().contains("로그인이 필요")) {
                    userCookie.setIsValid(false);

                    log.info("로그인이 풀린 사용자: {}, 사이트: {}, cookie: {}", userCookie.getUserName(), userCookie.getSiteName(), userCookie.getCookie());

                    slackService.sendMessage("[ " + userCookie.getUserName() + " ] 로그인 풀림.");
                } else if(response.getBody().contains("적립")) {
                    savePointLog(userCookie, response.getBody());
                }

                //cookie session값 갱신
                if(response.getHeaders().containsKey("cookie")) {
                    log.info("cookie 갱신 user: {}", userCookie.getUserName());
                    userCookie.setCookie(response.getHeaders().getFirst("cookie"));
                }

                log.debug("Response body: {} ", response.getBody());

                saveLog(url, userCookie, response);
            });
        }

        private void setCookieHeaders(HttpHeaders headers, String userCookie) {
            headers.add("Cookie", userCookie);
            headers.add("user-agent", "/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36");
        }

        private void saveLog(PointUrl url, UserCookie userCookie, ResponseEntity<String> response) {
            //사용자 별 호출 url 정보 저장
            pointUrlUserCookieRepository.save(PointUrlUserCookie.builder()
                    .pointUrl(url)
                    .userCookie(userCookie)
                    .build());

            //호출 log
            pointUrlCallLogRepository.save(PointUrlCallLog.builder()
                    .cookie(userCookie.getCookie())
                    .responseBody(response.getBody())
                    .responseHeader(response.getHeaders().toString())
                    .responseStatusCode(response.getStatusCode().value())
                    .siteName(userCookie.getSiteName())
                    .userName(userCookie.getUserName())
                    .cookie(userCookie.getCookie())
                    .pointUrl(url.getUrl())
                    .build()
            );
        }

        private void savePointLog(UserCookie userCookie, String body) {
            //alert('~~~ 10 원이 적립되었습니다.');

            savedPointRepository.save(SavedPoint.builder()
                    .point("코드 수정 필요")
                    .userCookie(userCookie)
                    .build());
        }
    }


}

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

    private final PointUrlUserCookieRepository pointUrlUserCookieRepository;

    private final PointUrlCallLogRepository pointUrlCallLogRepository;

    private final SavedPointRepository savedPointRepository;

    private final SlackService slackService;

    /**
     * 포인트 저장 로직
     */
    public void savePoint() {
        savePoint("naver");
    }

    /**
     * pointUrls의 데이터로 부터 포인터 적립
     */
    private void savePoint(String urlName) {
        List<PointUrl> pointUrls = pointUrlRepository.findByName(urlName);

        List<UserCookie> userCookies = userCookieRepository.findBySiteNameAndIsValid(urlName, true);

        pointUrls.forEach(url -> {
            log.info("Url: {}", url);

            savePointUser(url, userCookies);
        });
    }

    private void savePointUser(PointUrl url, List<UserCookie> userCookies) {
        userCookies.forEach(userCookie -> {
            //TODO 쿼리 한번에 데이터를 가져오도록 수정.
            //사용자가 이미 접속한 URL인 경우 제외
            if(!url.getPermanent() && pointUrlUserCookieRepository.findByPointUrlAndUserCookie(url, userCookie).isPresent()) {
                log.info("이미 수집된 URL. url: {}, user: {}", url.getUrl(), userCookie.getUserName());
                return;
            }

            log.info("Call point url: {}. user name: {}", url.getUrl(), userCookie.getUserName());

            exchange(url, userCookie);
        });
    }

    @Transactional
    protected void exchange(PointUrl url, UserCookie userCookie) {
        WebClient webClient = WebClient.create();

        Mono<ResponseEntity<String>> entityMono = webClient
                .get()
                .uri(URI.create(url.getUrl()))
                .headers(httpHeaders -> setHeaders(httpHeaders, userCookie))
                .retrieve()
                .toEntity(String.class)
        ;

        entityMono.subscribe(response -> {
            if(response == null || response.getBody() == null) {
                return;
            }

            if(response.getBody().contains("포인트 지급을 위해서는 로그인이 필요합니다")) {
                userCookie.setIsValid(false);

                log.info("로그인이 풀린 사용자: {}, 사이트: {}, cookie: {}", userCookie.getUserName(), userCookie.getSiteName(), userCookie.getCookie());

                slackService.sendMessage("[ " + userCookie.getUserName() + " ] 로그인 풀림.");
            } else if(response.getBody().contains("적립")) {
                savePointLog(userCookie);
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

    @Transactional
    protected void savePointLog(UserCookie userCookie) {
        savedPointRepository.save(SavedPoint.builder()
                .point("코드 수정 필요")
                .userCookie(userCookie)
                .build());
    }

    @Transactional
    protected void saveLog(PointUrl url, UserCookie userCookie, ResponseEntity<String> response) {
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

    private void setHeaders(HttpHeaders headers, UserCookie userCookie) {
        headers.add("Cookie", userCookie.getCookie());
        headers.add("user-agent", "/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36");
    }

}

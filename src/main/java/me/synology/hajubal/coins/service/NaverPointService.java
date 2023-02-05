package me.synology.hajubal.coins.service;

import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.entity.*;
import me.synology.hajubal.coins.respository.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.NullableUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.springframework.http.HttpMethod.GET;

/**
 * naver point url에 접속하여 point를 적립하는 로직
 */
@Slf4j
@Transactional(readOnly = true)
@Service
public class NaverPointService {

    @Autowired
    private UserCookieRepository userCookieRepository;

    @Autowired
    private PointUrlRepository pointUrlRepository;

    @Autowired
    private PointUrlUserCookieRepository pointUrlUserCookieRepository;

    @Autowired
    private PointUrlCallLogRepository pointUrlCallLogRepository;

    @Autowired
    private SavedPointRepository savedPointRepository;

    @Autowired
    private SlackService slackService;

    /**
     * 포인트 저장 로직
     */
    @Transactional
    public void savePoint() {
        List<PointUrl> pointUrls = pointUrlRepository.findByName("naver");

        savePoint(pointUrls);
    }

    /**
     * pointUrls의 데이터로 부터 포인터 적립
     * @param pointUrls
     */
    private void savePoint(List<PointUrl> pointUrls) {
        List<UserCookie> userCookies = userCookieRepository.findBySiteName("naver");

        pointUrls.forEach(url -> {
            log.info("Url: {}", url);

            savePointUser(url, userCookies);
        });
    }

    private void savePointUser(PointUrl url, List<UserCookie> userCookies) {
        userCookies.forEach(userCookie -> {
            if(!userCookie.getIsValid()) {
                log.warn("[{}] 사용자, 로그인이 풀려 point url 호출하지 않음", userCookie.getUserName());
                return;
            }

            //TODO 쿼리 한번에 데이터를 가져오도록 수정.
            //사용자가 이미 접속한 URL인 경우 제외
            if(!url.getPermanent() && pointUrlUserCookieRepository.findByPointUrlAndUserCookie(url, userCookie).isPresent()) {
                log.info("이미 수집된 URL. url: {}, user: {}", url.getUrl(), userCookie.getUserName());
                return;
            }

            log.info("call point url: {}. user name: {}", url.getUrl(), userCookie.getUserName());

            try {
                ResponseEntity<String> response = getResponseEntity(url, userCookie);

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

                log.debug("response: {} ", response.getBody());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }

            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @NotNull
    private ResponseEntity<String> getResponseEntity(PointUrl url, UserCookie userCookie) {
        ResponseEntity<String> response =  new RestTemplate().exchange(url.getUrl(), GET, new HttpEntity<String>(getHeaders(userCookie)), String.class);

        if(response.getBody() == null) {
            return ResponseEntity.noContent().build();
        }

        if(response.getBody().contains("포인트 지급을 위해서는 로그인이 필요합니다")) {
            userCookie.setIsValid(false);

            log.info("로그인이 풀린 사용자: {}, 사이트: {}, cookie: {}", userCookie.getUserName(), userCookie.getSiteName(), userCookie.getCookie());

            slackService.sendMessage("[ " + userCookie.getUserName() + " ] 로그인 풀림.");
        } else if(response.getBody().contains("적립")) {
            savedPointRepository.save(SavedPoint.builder()
                    .point("코드 수정 필요")
                    .userCookie(userCookie)
                    .build());
        }

        //cookie session값 갱신
        if(response.getHeaders().containsKey("cookie")) {
            log.info("cookie 갱신 user: {}", userCookie.getUserName());
            userCookie.setCookie(response.getHeaders().getFirst("cookie"));
        }

        return response;
    }

    @NotNull
    private static HttpHeaders getHeaders(UserCookie userCookie) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", userCookie.getCookie());
        headers.add("user-agent", "/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36");
        return headers;
    }
}

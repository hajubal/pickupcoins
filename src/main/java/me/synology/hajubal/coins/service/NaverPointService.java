package me.synology.hajubal.coins.service;

import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.entity.PointUrl;
import me.synology.hajubal.coins.entity.PointUrlCallLog;
import me.synology.hajubal.coins.entity.PointUrlUserCookie;
import me.synology.hajubal.coins.entity.UserCookie;
import me.synology.hajubal.coins.respository.PointUrlCallLogRepository;
import me.synology.hajubal.coins.respository.PointUrlRepository;
import me.synology.hajubal.coins.respository.PointUrlUserCookieRepository;
import me.synology.hajubal.coins.respository.UserCookieRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
    private SlackService slackService;

    @Transactional
    public void savePoint() {
        HttpHeaders headers = new HttpHeaders();
        RestTemplate restTemplate = new RestTemplate();

        //TODO point site url 별로 구분해서 조회
        List<PointUrl> pointUrls = pointUrlRepository.findAll();

        pointUrls.forEach(url -> {
            //사용자 cookie 목록
            List<UserCookie> userCookies = userCookieRepository.findBySiteName("naver");

            log.info("url: {}", url);

            for (UserCookie userCookie: userCookies) {
                if(!userCookie.getIsValid()) {
                    log.warn("[{}] 사용자, 로그인이 풀려 point url 호출하지 않음", userCookie.getUserName());
                    continue;
                }

                //TODO 쿼리 한번에 데이터를 가져오도록 수정.
                //이미 접속한 URL인 경우 제외
                if(!url.getPermanent() && pointUrlUserCookieRepository.findByPointUrlAndUserCookieUserName(url.getUrl(), userCookie.getUserName()).isPresent()) continue;

                log.info("Url: {}", url);
                log.info("call point url: {}. user name: {}", url.getUrl(), userCookie.getUserName());

                headers.clear();
                headers.add("Cookie", userCookie.getCookie());
                headers.add("user-agent", "/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36");

                ResponseEntity<String> response = null;

                try {
                    response = restTemplate.exchange(url.getUrl(), GET, new HttpEntity<String>(headers), String.class);
                } catch (HttpClientErrorException e) {
                    log.error(e.getMessage(), e);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }

                if(response.getBody().contains("포인트 지급을 위해서는 로그인이 필요합니다")) {
                    userCookie.setIsValid(false);

                    log.info("로그인이 풀린 사용자: {}, 사이트: {}, cookie: {}", userCookie.getUserName(), userCookie.getSiteName(), userCookie.getCookie());

                    slackService.sendMessage("[ " + userCookie.getUserName() + " ] 로그인 풀림.");
                }

                //cookie session값 갱신
                if(response.getHeaders().containsKey("cookie")) {
                    log.info("cookie 갱신 user: {}", userCookie.getUserName());
                    userCookie.setCookie(response.getHeaders().getFirst("cookie"));
                }

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

                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}

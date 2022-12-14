package me.synology.hajubal.coins.service;

import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.entity.PointUrl;
import me.synology.hajubal.coins.entity.PointUrlUserCookie;
import me.synology.hajubal.coins.entity.UserCookie;
import me.synology.hajubal.coins.respository.PointUrlUserCookieRepository;
import me.synology.hajubal.coins.respository.UserCookieRepository;
import me.synology.hajubal.coins.respository.PointUrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.springframework.http.HttpMethod.GET;

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
    private SlackService slackService;

    //cookie usernamedl 로그인 유효 여부
    private static final Map<String, Boolean> userAuth = new ConcurrentHashMap<>();

    /**
     * 로그인한 사용자
     * @param userName
     */
    public static void loginUser(String userName) {
        userAuth.put(userName, Boolean.TRUE);
    }

    /**
     * 로그인이 풀린 사용자
     *
     * @param userName
     */
    public static void logoutUser(String userName) {
        userAuth.put(userName, Boolean.FALSE);
    }

    @Transactional
    public void savePoint() {
        HttpHeaders headers = new HttpHeaders();
        RestTemplate restTemplate = new RestTemplate();

        List<PointUrl> pointUrls = pointUrlRepository.findAll();

        log.info("pointUrls count: {}", pointUrls.size());

        pointUrls.forEach(url -> {
            List<UserCookie> userCookies = userCookieRepository.findBySiteName("naver");

            for (UserCookie userCookie: userCookies) {
                if(!userAuth.get(userCookie.getUserName())) {
                    continue;
                }

                //TODO 쿼리 한번에 데이터를 가져오도록 수정.
                if(!pointUrlUserCookieRepository.findByPointUrlAndUserCookieUserName(url.getUrl(), userCookie.getUserName()).isEmpty()) continue;

                headers.clear();
                headers.add("Cookie", userCookie.getCookie());
                headers.add("user-agent", "/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36");

                ResponseEntity<String> response = restTemplate.exchange(url.getUrl(), GET, new HttpEntity<String>(headers), String.class);

                if(response.getBody().contains("포인트 지급을 위해서는 로그인이 필요합니다")) {
                    logoutUser(userCookie.getUserName());

                    log.info("로그인이 풀린 사용자: {}, 사이트: {}, cookie: {}", userCookie.getUserName(), userCookie.getSiteName(), userCookie.getCookie());

                    slackService.sendMessage("로그인 풀림.");
                } else if(response.getBody().contains("클릭적립은 캠페인당 1회만 적립됩니다.")) {
                    log.info("클릭적립은 캠페인당 1회만 적립됩니다.");

                    pointUrlUserCookieRepository.save(PointUrlUserCookie.builder().pointUrl(url.getUrl())
                            .userName(userCookie.getUserName()).build());
                }

                log.debug("response: {} ", response);

                log.info("call point url. user name: {}", userCookie.getUserName());

                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}

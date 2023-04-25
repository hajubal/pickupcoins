package me.synology.hajubal.coins.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.entity.PointUrl;
import me.synology.hajubal.coins.entity.UserCookie;
import me.synology.hajubal.coins.respository.PointUrlRepository;
import me.synology.hajubal.coins.respository.UserCookieRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

}

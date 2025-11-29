package me.synology.hajubal.coins.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.code.SiteName;
import me.synology.hajubal.coins.entity.Cookie;
import me.synology.hajubal.coins.entity.PointUrl;
import me.synology.hajubal.coins.respository.CookieRepository;
import me.synology.hajubal.coins.respository.PointUrlRepository;
import me.synology.hajubal.coins.dto.ExchangeDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * naver point url에 접속하여 point를 적립하는 로직
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class NaverSavePointService {

    private final CookieRepository cookieRepository;

    private final PointUrlRepository pointUrlRepository;

    private final ExchangeService exchangeService;

    /**
     * 포인트 저장 로직
     */
    @Transactional
    public void savePoint() {
        List<Cookie> cookies = cookieRepository.findBySiteNameAndIsValid(SiteName.NAVER.name(), true);

        log.debug("UserCookies: {}", cookies);

        List<ExchangeDto> exchangeDtoList = cookies.stream().map(ExchangeDto::from).toList();

        for (ExchangeDto exchangeDto : exchangeDtoList) {
            List<PointUrl> pointUrls = pointUrlRepository.findByNotCalledUrl(SiteName.NAVER.name(), exchangeDto.userName());

            log.info("Not called url size: {}", pointUrls.size());

            pointUrls.forEach(pointUrl -> exchangeService.exchange(pointUrl, exchangeDto));
        }
    }

}

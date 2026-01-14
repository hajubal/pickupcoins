package me.synology.hajubal.coins.service;

import java.net.URI;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.config.NaverPointProperties;
import me.synology.hajubal.coins.entity.*;
import me.synology.hajubal.coins.exception.PointExchangeException;
import me.synology.hajubal.coins.respository.PointUrlCallLogRepository;
import me.synology.hajubal.coins.respository.PointUrlCookieRepository;
import me.synology.hajubal.coins.dto.ExchangeDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * 포인트를 제공하는 url을 호출하는 서비스
 */
@Slf4j
@Transactional(readOnly = true)
@Service
public class ExchangeService {
  private final PointUrlCookieRepository pointUrlCookieRepository;
  private final PointUrlCallLogRepository pointUrlCallLogRepository;
  private final PointManageService pointManageService;
  private final CookieService cookieService;
  private final CookieNotificationService cookieNotificationService;
  private final NaverPointProperties naverPointProperties;

  // WebClient를 싱글톤으로 관리
  private final WebClient webClient;

  public ExchangeService(
      PointUrlCookieRepository pointUrlCookieRepository,
      PointUrlCallLogRepository pointUrlCallLogRepository,
      PointManageService pointManageService,
      CookieService cookieService,
      CookieNotificationService cookieNotificationService,
      NaverPointProperties naverPointProperties,
      WebClient.Builder webClientBuilder) {
    this.pointUrlCookieRepository = pointUrlCookieRepository;
    this.pointUrlCallLogRepository = pointUrlCallLogRepository;
    this.pointManageService = pointManageService;
    this.cookieService = cookieService;
    this.cookieNotificationService = cookieNotificationService;
    this.naverPointProperties = naverPointProperties;
    this.webClient = webClientBuilder.build();
  }

  /**
   * point url을 호출하고 결과를 처리
   *
   * @param url 포인트 URL
   * @param exchangeDto 교환 정보
   */
  @Transactional
  public void exchange(PointUrl url, ExchangeDto exchangeDto) {
    log.debug("Calling point URL. url: {}, user: {}", url.getUrl(), exchangeDto.userName());

    try {
      // block()으로 동기 처리하여 트랜잭션 경계 명확화
      ResponseEntity<String> response =
          webClient
              .get()
              .uri(URI.create(url.getUrl()))
              .headers(httpHeaders -> setCookieHeaders(httpHeaders, exchangeDto.cookie()))
              .retrieve()
              .toEntity(String.class)
              .block(naverPointProperties.getExchangeTimeout());

      if (response == null || !StringUtils.hasText(response.getBody())) {
        log.warn("Exchange response is empty. url: {}", url.getUrl());
        return;
      }

      String body = response.getBody();
      processResponse(body, exchangeDto, response);
      saveLog(url, exchangeDto.cookieId(), response);

      log.info(
          "Point exchange completed. user: {}, url: {}", exchangeDto.userName(), url.getUrl());

    } catch (Exception e) {
      log.error("Failed to exchange point. url: {}, user: {}", url.getUrl(), exchangeDto.userName(), e);
      throw new PointExchangeException("Failed to exchange point for url: " + url.getUrl(), e);
    }
  }

  /**
   * 응답 내용을 분석하여 적절한 후속 처리 수행
   */
  private void processResponse(
      String body, ExchangeDto exchangeDto, ResponseEntity<String> response) {
    if (isInvalidCookie(body)) {
      log.warn("Cookie is invalid. user: {}", exchangeDto.userName());
      cookieNotificationService.invalidateAndNotify(exchangeDto.cookieId(), exchangeDto.webHookUrl());
    } else if (isSavePoint(body)) {
      log.debug("Point saved successfully. user: {}", exchangeDto.userName());
      pointManageService.savePointPostProcess(exchangeDto, response);
    } else {
      log.debug("No point action needed. user: {}", exchangeDto.userName());
    }
  }

  /**
   * 포인트 적립 성공 여부 확인
   */
  private boolean isSavePoint(String content) {
    return content.contains(naverPointProperties.getSaveKeyword());
  }

  /**
   * 쿠키 무효화 여부 확인
   */
  private boolean isInvalidCookie(String content) {
    return content.contains(naverPointProperties.getInvalidCookieKeyword());
  }

  /**
   * HTTP 헤더에 쿠키 및 User-Agent 설정
   */
  private void setCookieHeaders(HttpHeaders headers, String userCookie) {
    headers.add("Cookie", userCookie);
    headers.add("User-Agent", naverPointProperties.getUserAgent());
  }

  /**
   * 포인트 URL 호출 로그 저장
   * 트랜잭션은 상위 메서드(exchange)의 트랜잭션을 따름
   */
  private void saveLog(PointUrl url, Long cookieId, ResponseEntity<String> response) {
    Cookie cookie = cookieService.getCookie(cookieId);

    // 사용자별 호출 url 정보 저장
    pointUrlCookieRepository.save(
        PointUrlCookie.builder().pointUrl(url).cookie(cookie).build());

    // 호출 log 저장
    pointUrlCallLogRepository.save(
        PointUrlCallLog.builder()
            .cookie(cookie.getCookie())
            .responseBody(response.getBody())
            .responseHeader(response.getHeaders().toString())
            .responseStatusCode(response.getStatusCode().value())
            .siteName(cookie.getSiteName())
            .userName(cookie.getUserName())
            .pointUrl(url.getUrl())
            .build());

    log.debug("Exchange log saved. user: {}, url: {}", cookie.getUserName(), url.getUrl());
  }
}

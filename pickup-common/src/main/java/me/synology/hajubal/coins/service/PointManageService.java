package me.synology.hajubal.coins.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.config.NaverPointProperties;
import me.synology.hajubal.coins.dto.ExchangeDto;
import me.synology.hajubal.coins.entity.Cookie;
import me.synology.hajubal.coins.entity.SavedPoint;
import me.synology.hajubal.coins.respository.SavedPointRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
@Service
public class PointManageService {

  private final CookieService cookieService;
  private final SavedPointRepository savedPointRepository;
  private final NaverPointProperties naverPointProperties;

  /**
   * 포인트 적립 후 처리
   * - 쿠키 갱신 (필요 시)
   * - 적립 포인트 정보 저장
   */
  @Transactional
  public void savePointPostProcess(ExchangeDto exchangeDto, ResponseEntity<String> response) {
    // cookie session값 갱신
    if (response.getHeaders().containsKey("Set-Cookie")) {
      String newCookie = response.getHeaders().getFirst("Set-Cookie");
      log.debug("Updating cookie. user: {}", exchangeDto.userName());
      cookieService.updateCookie(exchangeDto.cookieId(), newCookie);
    }

    Cookie cookie = cookieService.getCookie(exchangeDto.cookieId());
    int amount = extractAmount(response.getBody());

    // 적립 포인트 저장
    savedPointRepository.save(
        SavedPoint.builder()
            .amount(amount)
            .cookie(cookie)
            .responseBody(response.getBody())
            .build());

    log.info("Point saved. user: {}, amount: {}원", exchangeDto.userName(), amount);
  }

  /**
   * 응답 본문에서 적립 포인트 금액 추출
   *
   * @param body 응답 본문 (예: "10원이 적립 됩니다.")
   * @return 추출된 금액 (예: 10), 추출 실패 시 0
   */
  private int extractAmount(String body) {
    Pattern pattern = Pattern.compile(naverPointProperties.getAmountPattern());
    Matcher matcher = pattern.matcher(body);

    if (matcher.find()) {
      String matched = matcher.group().replace("원이 적립 됩니다.", "").trim();
      return Integer.parseInt(matched);
    }

    log.warn("Failed to extract amount from response body: {}", body);
    return 0;
  }
}

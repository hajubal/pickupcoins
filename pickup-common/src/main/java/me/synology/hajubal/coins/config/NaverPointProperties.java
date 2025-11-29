package me.synology.hajubal.coins.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 네이버 포인트 관련 설정 값
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "naver.point")
public class NaverPointProperties {

  /**
   * 포인트 적립 성공 여부를 판단하는 키워드
   */
  private String saveKeyword = "적립";

  /**
   * 쿠키 무효화를 판단하는 키워드
   */
  private String invalidCookieKeyword = "로그인이 필요";

  /**
   * 포인트 금액을 추출하는 정규식 패턴
   */
  private String amountPattern = "\\s\\d+원이 적립 됩니다.";

  /**
   * HTTP 요청 시 사용할 User-Agent
   */
  private String userAgent =
      "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36";
}

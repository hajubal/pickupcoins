package me.synology.hajubal.coins.config;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 웹 크롤러 관련 설정 값
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "crawler")
public class CrawlerProperties {

  /**
   * 크롤링 타임아웃 (밀리초)
   */
  private int timeout = 10000;

  /**
   * 크롤링 실패 시 재시도 횟수
   */
  private int retryCount = 3;

  /**
   * 포인트 URL 식별 키워드
   */
  private List<String> pointUrlKeywords = new ArrayList<>();
}

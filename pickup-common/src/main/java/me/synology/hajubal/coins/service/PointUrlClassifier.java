package me.synology.hajubal.coins.service;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.config.CrawlerProperties;
import me.synology.hajubal.coins.config.NaverPointProperties;
import me.synology.hajubal.coins.entity.type.POINT_URL_TYPE;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class PointUrlClassifier {

  private final NaverPointProperties naverPointProperties;
  private final CrawlerProperties crawlerProperties;

  /**
   * URL이 포인트 URL인지 확인
   *
   * @param url URL 문자열
   * @return 포인트 URL이면 true
   */
  public boolean isPointUrl(String url) {
    if (url == null) {
      return false;
    }

    for (String keyword : crawlerProperties.getPointUrlKeywords()) {
      if (url.contains(keyword)) {
        return true;
      }
    }

    return false;
  }

  /**
   * URL을 분류하여 POINT_URL_TYPE 반환
   *
   * @param url URL 문자열
   * @return POINT_URL_TYPE
   */
  public POINT_URL_TYPE classify(String url) {
    Map<String, List<String>> types = naverPointProperties.getTypes();

    for (Map.Entry<String, List<String>> entry : types.entrySet()) {
      String typeName = entry.getKey();
      List<String> patterns = entry.getValue();

      for (String pattern : patterns) {
        if (url.contains(pattern)) {
          try {
            return POINT_URL_TYPE.valueOf(typeName);
          } catch (IllegalArgumentException e) {
            log.warn("Invalid POINT_URL_TYPE in properties: {}", typeName);
          }
        }
      }
    }

    return POINT_URL_TYPE.UNSUPPORT;
  }
}

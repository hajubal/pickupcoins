package me.synology.hajubal.coins.admin.config;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * CORS 설정 Properties
 *
 * <p>application.yml의 cors.* 설정을 바인딩합니다.
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "cors")
public class CorsProperties {

  /** 허용할 Origin 목록 */
  private List<String> allowedOrigins;

  /** 허용할 HTTP 메서드 목록 */
  private List<String> allowedMethods;

  /** 허용할 헤더 목록 */
  private List<String> allowedHeaders;

  /** 인증 정보(쿠키 등) 허용 여부 */
  private Boolean allowCredentials;
}

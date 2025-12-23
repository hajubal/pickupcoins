package me.synology.hajubal.coins.admin.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 설정 Properties
 *
 * <p>application.yml의 jwt.* 설정을 바인딩합니다.
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

  /** JWT 서명에 사용할 비밀키 (최소 256비트) */
  private String secret;

  /** Access Token 유효기간 (밀리초) */
  private Long accessTokenValidity;

  /** Refresh Token 유효기간 (밀리초) */
  private Long refreshTokenValidity;

  /** Remember Me Token 유효기간 (밀리초, 15일) */
  private Long rememberMeTokenValidity;
}

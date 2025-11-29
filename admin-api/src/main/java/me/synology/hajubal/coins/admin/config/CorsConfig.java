package me.synology.hajubal.coins.admin.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * CORS(Cross-Origin Resource Sharing) 설정
 *
 * <p>React 프론트엔드에서 API 호출을 허용하기 위한 CORS 설정입니다.
 */
@Configuration
@RequiredArgsConstructor
public class CorsConfig {

  private final CorsProperties corsProperties;

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    // 허용할 Origin 설정
    configuration.setAllowedOrigins(corsProperties.getAllowedOrigins());

    // 허용할 HTTP 메서드 설정
    configuration.setAllowedMethods(corsProperties.getAllowedMethods());

    // 허용할 헤더 설정
    configuration.setAllowedHeaders(corsProperties.getAllowedHeaders());

    // 인증 정보 허용 (쿠키, Authorization 헤더 등)
    configuration.setAllowCredentials(corsProperties.getAllowCredentials());

    // 모든 경로에 대해 CORS 설정 적용
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);

    return source;
  }
}

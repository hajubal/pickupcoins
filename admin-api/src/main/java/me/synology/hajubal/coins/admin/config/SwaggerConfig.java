package me.synology.hajubal.coins.admin.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI 설정
 *
 * <p>API 문서화 및 JWT 인증 스키마를 설정합니다.
 */
@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI openAPI() {
    // JWT 인증 스키마 정의
    SecurityScheme securityScheme =
        new SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")
            .in(SecurityScheme.In.HEADER)
            .name("Authorization");

    // Security Requirement 정의
    SecurityRequirement securityRequirement = new SecurityRequirement().addList("Bearer Token");

    return new OpenAPI()
        .info(
            new Info()
                .title("Admin API Server")
                .version("2.0.0")
                .description(
                    "포인트 수집 시스템 관리자 REST API 서버\n\n"
                        + "## 인증 방법\n"
                        + "1. POST /api/v1/auth/login 으로 로그인\n"
                        + "2. 응답받은 accessToken을 Authorization 헤더에 포함\n"
                        + "   - 형식: `Bearer {accessToken}`\n"
                        + "3. 토큰 만료 시 POST /api/v1/auth/refresh 로 갱신\n\n"
                        + "## 주요 기능\n"
                        + "- **인증**: JWT 기반 로그인/로그아웃/토큰갱신\n"
                        + "- **대시보드**: 통계 및 현황 조회\n"
                        + "- **쿠키 관리**: 사이트별 쿠키 CRUD\n"
                        + "- **포인트 관리**: URL 및 적립 로그 조회\n"
                        + "- **사이트 관리**: 사이트 정보 CRUD\n"
                        + "- **사용자 관리**: 계정 정보 및 설정"))
        .components(new Components().addSecuritySchemes("Bearer Token", securityScheme))
        .addSecurityItem(securityRequirement);
  }
}

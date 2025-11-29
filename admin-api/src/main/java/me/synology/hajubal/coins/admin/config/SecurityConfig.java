package me.synology.hajubal.coins.admin.config;

import lombok.RequiredArgsConstructor;
import me.synology.hajubal.coins.admin.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * Spring Security 설정
 *
 * <p>JWT 기반 인증을 사용하며, Stateless 세션 정책을 적용합니다.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final CorsConfigurationSource corsConfigurationSource;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        // CORS 설정 적용
        .cors(cors -> cors.configurationSource(corsConfigurationSource))

        // CSRF 비활성화 (JWT 사용으로 불필요)
        .csrf(AbstractHttpConfigurer::disable)

        // 세션 사용하지 않음 (Stateless)
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

        // 요청 인가 설정
        .authorizeHttpRequests(
            authorize ->
                authorize
                    // 인증 없이 접근 가능한 경로
                    .requestMatchers(
                        "/api/v1/auth/**", // 로그인, 토큰 갱신
                        "/swagger-ui/**", // Swagger UI
                        "/v3/api-docs/**", // OpenAPI 문서
                        "/actuator/health", // Health check
                        "/actuator/info" // Info
                        )
                    .permitAll()
                    // 나머지는 인증 필요
                    .anyRequest()
                    .authenticated())

        // JWT 인증 필터 추가
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public AuthenticationManager authenticationManager(
      AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}

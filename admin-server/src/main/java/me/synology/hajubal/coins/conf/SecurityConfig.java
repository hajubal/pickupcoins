package me.synology.hajubal.coins.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * Spring Security 설정
 * - 인증/인가 규칙 정의
 * - CSRF 보호 활성화
 * - 세션 관리 및 보안 헤더 설정
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Order(1)
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(
            (authorizeHttpRequests) ->
                authorizeHttpRequests
                    .requestMatchers("/actuator/health", "/actuator/info")
                    .permitAll()
                    .requestMatchers("/css/**", "/assets/**", "/js/**", "/images/**")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .httpBasic(withDefaults())
        .formLogin(
            loginConfigurer -> {
              loginConfigurer
                  .loginPage("/login")
                  .defaultSuccessUrl("/dashboard", true)
                  .permitAll();
            })
        .logout(
            logoutConfigurer -> {
              logoutConfigurer
                  .logoutUrl("/logout")
                  .logoutSuccessUrl("/login?logout")
                  .invalidateHttpSession(true)
                  .deleteCookies("JSESSIONID")
                  .permitAll();
            })
        .sessionManagement(
            sessionManagement -> {
              sessionManagement
                  .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                  .maximumSessions(1)
                  .maxSessionsPreventsLogin(false);
            })
        .headers(
            headers -> {
              headers
                  .frameOptions(frameOptions -> frameOptions.sameOrigin())
                  .xssProtection(withDefaults())
                  .contentSecurityPolicy(
                      csp -> csp.policyDirectives("default-src 'self'; script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline'"));
            });

    return http.build();
  }
}

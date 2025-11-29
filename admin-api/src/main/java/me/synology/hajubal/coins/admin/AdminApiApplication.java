package me.synology.hajubal.coins.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Admin API Server Application
 *
 * <p>포인트 수집 시스템의 관리자 REST API 서버
 *
 * <ul>
 *   <li>포트: 8082 (application.yml 설정)
 *   <li>인증: JWT 기반 (Access Token + Refresh Token)
 *   <li>API 문서: /swagger-ui.html
 * </ul>
 */
@SpringBootApplication(scanBasePackages = "me.synology.hajubal.coins")
@EntityScan(basePackages = "me.synology.hajubal.coins.entity")
@EnableJpaRepositories(basePackages = "me.synology.hajubal.coins.respository")
public class AdminApiApplication {

  public static void main(String[] args) {
    SpringApplication.run(AdminApiApplication.class, args);
  }
}

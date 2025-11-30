package me.synology.hajubal.coins.admin.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.entity.SiteUser;
import me.synology.hajubal.coins.respository.SiteUserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 개발 환경 초기 데이터 생성
 */
@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

  private final SiteUserRepository siteUserRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public void run(ApplicationArguments args) {
    // 이미 사용자가 있으면 스킵
    if (siteUserRepository.findByLoginId("admin").isPresent()) {
      log.info("Admin user already exists, skipping initialization");
      return;
    }

    // 테스트 사용자 생성
    SiteUser admin =
        SiteUser.builder()
            .loginId("admin")
            .userName("Administrator")
            .password(passwordEncoder.encode("test123"))
            .build();

    siteUserRepository.save(admin);
    log.info("Created test user: loginId=admin, password=test123");
  }
}

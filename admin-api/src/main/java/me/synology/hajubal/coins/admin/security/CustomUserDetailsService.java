package me.synology.hajubal.coins.admin.security;

import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.entity.SiteUser;
import me.synology.hajubal.coins.respository.SiteUserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Security UserDetailsService 구현
 *
 * <p>DB에서 사용자 정보를 조회하여 Spring Security의 UserDetails로 변환합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final SiteUserRepository siteUserRepository;

  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
    log.debug("Loading user by loginId: {}", loginId);

    SiteUser siteUser =
        siteUserRepository
            .findByLoginId(loginId)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + loginId));

    // 비활성화된 사용자 체크
    if (Boolean.FALSE.equals(siteUser.getActive())) {
      throw new UsernameNotFoundException("User is inactive: " + loginId);
    }

    // Spring Security UserDetails로 변환
    return User.builder()
        .username(siteUser.getLoginId())
        .password(siteUser.getPassword())
        .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
        .accountExpired(false)
        .accountLocked(false)
        .credentialsExpired(false)
        .disabled(!siteUser.getActive())
        .build();
  }
}

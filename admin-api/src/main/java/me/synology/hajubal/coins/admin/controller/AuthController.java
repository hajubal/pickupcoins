package me.synology.hajubal.coins.admin.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.admin.dto.LoginRequest;
import me.synology.hajubal.coins.admin.dto.LoginResponse;
import me.synology.hajubal.coins.admin.security.JwtTokenProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 인증 관련 API 컨트롤러
 *
 * <p>로그인, 토큰 갱신 등의 인증 관련 엔드포인트를 제공합니다.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthenticationManager authenticationManager;
  private final JwtTokenProvider jwtTokenProvider;

  /**
   * 로그인
   *
   * @param loginRequest 로그인 요청 (loginId, password)
   * @return JWT 토큰 및 사용자 정보
   */
  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
    log.info("Login attempt for user: {}", loginRequest.getLoginId());

    try {
      // 사용자 인증
      Authentication authentication =
          authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(
                  loginRequest.getLoginId(), loginRequest.getPassword()));

      // JWT 토큰 생성
      String accessToken = jwtTokenProvider.createAccessToken(authentication);
      String refreshToken = jwtTokenProvider.createRefreshToken(
          loginRequest.getLoginId(), loginRequest.isRememberMe());

      // UserDetails에서 사용자 정보 추출
      UserDetails userDetails = (UserDetails) authentication.getPrincipal();

      // 응답 생성
      LoginResponse response =
          LoginResponse.builder()
              .accessToken(accessToken)
              .refreshToken(refreshToken)
              .loginId(userDetails.getUsername())
              .userName(userDetails.getUsername()) // userName은 별도로 조회하거나 UserDetails 확장 필요
              .build();

      log.info("Login successful for user: {}", loginRequest.getLoginId());
      return ResponseEntity.ok(response);

    } catch (AuthenticationException e) {
      log.warn("Login failed for user: {}", loginRequest.getLoginId(), e);
      return ResponseEntity.status(401).build();
    }
  }
}

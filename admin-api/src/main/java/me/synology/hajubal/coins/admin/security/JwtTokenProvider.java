package me.synology.hajubal.coins.admin.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.admin.config.JwtProperties;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * JWT 토큰 생성 및 검증을 담당하는 Provider
 *
 * <p>Access Token과 Refresh Token을 생성하고 검증합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

  private final JwtProperties jwtProperties;
  private SecretKey secretKey;

  @PostConstruct
  protected void init() {
    // Secret Key를 HMAC-SHA 알고리즘에 맞게 생성
    this.secretKey =
        Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
  }

  /**
   * Access Token 생성
   *
   * @param authentication 인증 정보
   * @return JWT Access Token
   */
  public String createAccessToken(Authentication authentication) {
    return createToken(
        authentication.getName(), jwtProperties.getAccessTokenValidity(), "access");
  }

  /**
   * Refresh Token 생성
   *
   * @param username 사용자 이름
   * @return JWT Refresh Token
   */
  public String createRefreshToken(String username) {
    return createToken(username, jwtProperties.getRefreshTokenValidity(), "refresh");
  }

  /**
   * JWT 토큰 생성
   *
   * @param username 사용자 이름
   * @param validityInMilliseconds 유효기간 (밀리초)
   * @param tokenType 토큰 타입 (access/refresh)
   * @return JWT 토큰
   */
  private String createToken(String username, Long validityInMilliseconds, String tokenType) {
    Date now = new Date();
    Date validity = new Date(now.getTime() + validityInMilliseconds);

    return Jwts.builder()
        .subject(username)
        .claim("type", tokenType)
        .issuedAt(now)
        .expiration(validity)
        .signWith(secretKey, Jwts.SIG.HS256)
        .compact();
  }

  /**
   * JWT 토큰에서 사용자 이름 추출
   *
   * @param token JWT 토큰
   * @return 사용자 이름
   */
  public String getUsername(String token) {
    return getClaims(token).getSubject();
  }

  /**
   * JWT 토큰 검증
   *
   * @param token JWT 토큰
   * @return 유효성 여부
   */
  public boolean validateToken(String token) {
    try {
      getClaims(token);
      return true;
    } catch (SecurityException | MalformedJwtException e) {
      log.warn("Invalid JWT signature: {}", e.getMessage());
    } catch (ExpiredJwtException e) {
      log.warn("Expired JWT token: {}", e.getMessage());
    } catch (UnsupportedJwtException e) {
      log.warn("Unsupported JWT token: {}", e.getMessage());
    } catch (IllegalArgumentException e) {
      log.warn("JWT claims string is empty: {}", e.getMessage());
    }
    return false;
  }

  /**
   * JWT 토큰에서 Claims 추출
   *
   * @param token JWT 토큰
   * @return Claims
   */
  private Claims getClaims(String token) {
    return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
  }

  /**
   * Authentication 객체에서 사용자 이름 추출
   *
   * @param authentication 인증 정보
   * @return 사용자 이름
   */
  public String getUsernameFromAuthentication(Authentication authentication) {
    if (authentication.getPrincipal() instanceof UserDetails) {
      return ((UserDetails) authentication.getPrincipal()).getUsername();
    }
    return authentication.getName();
  }
}

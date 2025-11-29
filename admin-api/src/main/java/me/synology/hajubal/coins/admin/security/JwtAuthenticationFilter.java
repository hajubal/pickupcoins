package me.synology.hajubal.coins.admin.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * JWT 인증 필터
 *
 * <p>모든 HTTP 요청을 인터셉트하여 JWT 토큰을 검증하고 SecurityContext에 인증 정보를 설정합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private static final String AUTHORIZATION_HEADER = "Authorization";
  private static final String BEARER_PREFIX = "Bearer ";

  private final JwtTokenProvider jwtTokenProvider;
  private final UserDetailsService userDetailsService;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    try {
      // 요청 헤더에서 JWT 토큰 추출
      String jwt = resolveToken(request);

      // 토큰 검증 및 인증 정보 설정
      if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {
        String username = jwtTokenProvider.getUsername(jwt);

        // UserDetailsService를 통해 사용자 정보 로드
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // Authentication 객체 생성
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // SecurityContext에 인증 정보 설정
        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.debug("Set authentication for user: {}", username);
      }
    } catch (Exception e) {
      log.error("Cannot set user authentication: {}", e.getMessage());
    }

    filterChain.doFilter(request, response);
  }

  /**
   * HTTP 요청 헤더에서 JWT 토큰 추출
   *
   * @param request HTTP 요청
   * @return JWT 토큰 (Bearer 접두사 제거), 없으면 null
   */
  private String resolveToken(HttpServletRequest request) {
    String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
      return bearerToken.substring(BEARER_PREFIX.length());
    }

    return null;
  }
}

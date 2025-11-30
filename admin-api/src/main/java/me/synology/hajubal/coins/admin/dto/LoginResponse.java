package me.synology.hajubal.coins.admin.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * 로그인 응답 DTO
 *
 * <p>로그인 성공 시 클라이언트에 반환하는 JWT 토큰 및 사용자 정보입니다.
 */
@Getter
@Builder
public class LoginResponse {

  /** JWT 액세스 토큰 */
  private String accessToken;

  /** JWT 리프레시 토큰 */
  private String refreshToken;

  /** 사용자 이름 */
  private String userName;

  /** 로그인 ID */
  private String loginId;
}

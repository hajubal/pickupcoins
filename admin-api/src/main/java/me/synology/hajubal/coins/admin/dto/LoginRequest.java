package me.synology.hajubal.coins.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 로그인 요청 DTO
 *
 * <p>사용자 로그인 시 클라이언트에서 전송하는 요청 형식입니다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

  /** 로그인 ID */
  @NotBlank(message = "로그인 ID는 필수입니다")
  private String loginId;

  /** 비밀번호 */
  @NotBlank(message = "비밀번호는 필수입니다")
  private String password;

  /** 로그인 유지 여부 (15일간 자동 로그인) */
  private boolean rememberMe = false;
}

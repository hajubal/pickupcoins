package me.synology.hajubal.coins.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CookieRequest {
  @NotBlank(message = "사용자명은 필수입니다")
  private String userName;

  @NotBlank(message = "사이트명은 필수입니다")
  private String siteName;

  @NotBlank(message = "쿠키는 필수입니다")
  private String cookie;

  private Boolean isValid = true;
}

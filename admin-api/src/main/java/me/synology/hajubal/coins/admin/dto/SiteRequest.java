package me.synology.hajubal.coins.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SiteRequest {
  @NotBlank(message = "사이트 이름은 필수입니다")
  private String name;

  @NotBlank(message = "도메인은 필수입니다")
  private String domain;

  @NotBlank(message = "URL은 필수입니다")
  private String url;
}

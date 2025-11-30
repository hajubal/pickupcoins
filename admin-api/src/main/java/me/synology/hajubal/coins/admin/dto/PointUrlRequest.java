package me.synology.hajubal.coins.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PointUrlRequest {
  @NotBlank(message = "URL은 필수입니다")
  private String url;

  private Boolean permanent;
}

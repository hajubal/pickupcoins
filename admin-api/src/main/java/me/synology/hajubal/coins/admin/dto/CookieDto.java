package me.synology.hajubal.coins.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.synology.hajubal.coins.entity.Cookie;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CookieDto {
  private Long id;
  private String userName;
  private String siteName;
  private String cookie;
  private Boolean isValid;
  private LocalDateTime createdDate;
  private LocalDateTime modifiedDate;

  public static CookieDto from(Cookie cookie) {
    return CookieDto.builder()
        .id(cookie.getId())
        .userName(cookie.getUserName())
        .siteName(cookie.getSiteName())
        .cookie(cookie.getCookie())
        .isValid(cookie.getIsValid())
        .createdDate(cookie.getCreatedDate())
        .modifiedDate(cookie.getModifiedDate())
        .build();
  }
}

package me.synology.hajubal.coins.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.synology.hajubal.coins.entity.SavedPoint;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavedPointDto {
  private Long id;
  private Long cookieId;
  private String cookieUserName;
  private String cookieSiteName;
  private int amount;
  private String responseBody;
  private LocalDateTime createdDate;
  private LocalDateTime modifiedDate;

  public static SavedPointDto from(SavedPoint savedPoint) {
    return SavedPointDto.builder()
        .id(savedPoint.getId())
        .cookieId(savedPoint.getCookie() != null ? savedPoint.getCookie().getId() : null)
        .cookieUserName(
            savedPoint.getCookie() != null ? savedPoint.getCookie().getUserName() : null)
        .cookieSiteName(
            savedPoint.getCookie() != null ? savedPoint.getCookie().getSiteName() : null)
        .amount(savedPoint.getAmount())
        .responseBody(savedPoint.getResponseBody())
        .createdDate(savedPoint.getCreatedDate())
        .modifiedDate(savedPoint.getModifiedDate())
        .build();
  }
}

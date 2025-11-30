package me.synology.hajubal.coins.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.synology.hajubal.coins.entity.PointUrl;
import me.synology.hajubal.coins.entity.type.POINT_URL_TYPE;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointUrlDto {
  private Long id;
  private String name;
  private String url;
  private String pointUrlType;
  private Boolean permanent;
  private LocalDateTime createdDate;
  private LocalDateTime modifiedDate;

  public static PointUrlDto from(PointUrl pointUrl) {
    return PointUrlDto.builder()
        .id(pointUrl.getId())
        .name(pointUrl.getName())
        .url(pointUrl.getUrl())
        .pointUrlType(pointUrl.getPointUrlType() != null ? pointUrl.getPointUrlType().name() : null)
        .permanent(pointUrl.getPermanent())
        .createdDate(pointUrl.getCreatedDate())
        .modifiedDate(pointUrl.getModifiedDate())
        .build();
  }
}

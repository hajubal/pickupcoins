package me.synology.hajubal.coins.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.synology.hajubal.coins.entity.Site;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SiteDto {
  private Long id;
  private String name;
  private String domain;
  private String url;
  private LocalDateTime createdDate;
  private LocalDateTime modifiedDate;

  public static SiteDto from(Site site) {
    return SiteDto.builder()
        .id(site.getId())
        .name(site.getName())
        .domain(site.getDomain())
        .url(site.getUrl())
        .createdDate(site.getCreatedDate())
        .modifiedDate(site.getModifiedDate())
        .build();
  }
}

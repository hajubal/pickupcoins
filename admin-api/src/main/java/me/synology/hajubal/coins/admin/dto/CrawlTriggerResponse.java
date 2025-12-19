package me.synology.hajubal.coins.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrawlTriggerResponse {
    private String status;
    private String message;
    private Long durationMs;
}

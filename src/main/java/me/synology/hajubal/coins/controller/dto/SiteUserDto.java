package me.synology.hajubal.coins.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

public class SiteUserDto {

    @Data
    public static class UpdateDto {

        private Long id;

        private String loginId;

        @NotBlank
        @Size(max = 255)
        private String userName;

        @Size(max = 255)
        private String slackWebhookUrl;
    }
}

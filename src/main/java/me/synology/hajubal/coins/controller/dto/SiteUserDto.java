package me.synology.hajubal.coins.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import me.synology.hajubal.coins.entity.SiteUser;

public class SiteUserDto {

    @Data
    public static class UpdateDto {

        private Long id;

        private String loginId;

        private Boolean active;

        @NotBlank
        @Size(max = 255)
        private String userName;

        @Size(max = 255)
        private String slackWebhookUrl;

        public static UpdateDto fromEntity(SiteUser siteUser) {
            UpdateDto updateDto = new UpdateDto();
            updateDto.setId(siteUser.getId());
            updateDto.setUserName(siteUser.getUserName());
            updateDto.setLoginId(siteUser.getLoginId());
            updateDto.setSlackWebhookUrl(siteUser.getSlackWebhookUrl());
            updateDto.setActive(siteUser.getActive());

            return updateDto;
        }
    }
}

package me.synology.hajubal.coins.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import me.synology.hajubal.coins.entity.Cookie;
import me.synology.hajubal.coins.entity.SiteUser;

public class UserCookieDto {

    @Data
    public static class InsertDto {
        @NotBlank
        @Size(max = 255)
        private String userName;

        @NotBlank
        @Size(max = 255)
        private String siteName;

        @NotBlank
        private String cookie;

        public Cookie toEntity(SiteUser siteUser) {
            return Cookie.builder()
                    .userName(userName)
                    .siteName(siteName)
                    .cookie(cookie)
                    .isValid(Boolean.TRUE)
                    .siteUser(siteUser)
                    .build();
        }
    }

    @Data
    public static class UpdateDto {

        @NotNull
        private Long id;

        @NotBlank
        @Size(max = 255)
        private String userName;

        @NotBlank
        @Size(max = 255)
        private String siteName;

        @NotBlank
        private String cookie;

        @NotNull
        private Boolean isValid;
    }
}

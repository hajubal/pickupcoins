package me.synology.hajubal.coins.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import me.synology.hajubal.coins.entity.UserCookie;

public class CookieDto {

    @Data
    public static class CookieInsertDto {
        @NotBlank
        @Size(max = 255)
        private String userName;

        @NotBlank
        @Size(max = 255)
        private String siteName;

        @NotBlank
        private String cookie;

        public UserCookie toEntity() {
            return UserCookie.builder()
                    .userName(userName)
                    .siteName(siteName)
                    .cookie(cookie)
                    .isValid(Boolean.TRUE)
                    .build();
        }
    }

    @Data
    public class CookieUpdateDto {

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

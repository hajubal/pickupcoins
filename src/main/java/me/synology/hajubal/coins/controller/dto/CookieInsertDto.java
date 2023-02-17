package me.synology.hajubal.coins.controller.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import me.synology.hajubal.coins.entity.UserCookie;

@Data
public class CookieInsertDto {
    @NotBlank
    @Max(255)
    private String userName;

    @NotBlank
    @Max(255)
    private String siteName;

    @NotBlank
    private String cookie;

    private Boolean isValid;

    public UserCookie toEntity() {
        return UserCookie.builder()
                .userName(userName)
                .siteName(siteName)
                .cookie(cookie)
                .isValid(isValid)
                .build();
    }
}

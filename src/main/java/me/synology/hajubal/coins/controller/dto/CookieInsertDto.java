package me.synology.hajubal.coins.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import me.synology.hajubal.coins.entity.UserCookie;

@Data
public class CookieInsertDto {
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

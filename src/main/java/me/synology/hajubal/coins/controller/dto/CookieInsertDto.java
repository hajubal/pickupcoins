package me.synology.hajubal.coins.controller.dto;

import lombok.Data;
import me.synology.hajubal.coins.entity.UserCookie;

@Data
public class CookieInsertDto {
    private String userName;

    private String siteName;

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

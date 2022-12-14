package me.synology.hajubal.coins.controller.dto;

import lombok.Data;
import me.synology.hajubal.coins.entity.UserCookie;

@Data
public class CookieUpdateDto {

    private String userName;

    private String siteName;

    private String cookie;

}

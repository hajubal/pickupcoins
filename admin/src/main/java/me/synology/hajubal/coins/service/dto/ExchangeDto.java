package me.synology.hajubal.coins.service.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.synology.hajubal.coins.entity.Cookie;

@RequiredArgsConstructor
@Getter
public class ExchangeDto {

    private final Long cookieId;

    private final String userName;

    private final String cookie;

    private final String webHookUrl;

    private final String siteName;

    public static ExchangeDto from(Cookie cookie) {
        return new ExchangeDto(cookie.getId(), cookie.getUserName(), cookie.getCookie(), cookie.getSiteUser().getSlackWebhookUrl(), cookie.getSiteName());
    }
}

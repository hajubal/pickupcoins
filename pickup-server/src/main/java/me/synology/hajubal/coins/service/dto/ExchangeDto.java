package me.synology.hajubal.coins.service.dto;

import me.synology.hajubal.coins.entity.Cookie;

public record ExchangeDto(Long cookieId, String userName, String cookie, String webHookUrl, String siteName) {

    public static ExchangeDto from(Cookie cookie) {
        return new ExchangeDto(cookie.getId(), cookie.getUserName(), cookie.getCookie(), cookie.getSiteUser().getSlackWebhookUrl(), cookie.getSiteName());
    }
}

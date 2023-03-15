package me.synology.hajubal.coins.crawler.impl;

import me.synology.hajubal.coins.crawler.SiteData;

public class ClientSiteData implements SiteData {
    @Override
    public String getSiteName() {
        return "클리앙";
    }

    @Override
    public String getDomain() {
        return "https://www.clien.net";
    }

    @Override
    public String getBoardUrl() {
        return "/service/board/jirum";
    }
}

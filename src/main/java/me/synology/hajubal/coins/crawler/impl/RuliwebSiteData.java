package me.synology.hajubal.coins.crawler.impl;

import me.synology.hajubal.coins.crawler.SiteData;
import org.springframework.stereotype.Component;

@Component
public class RuliwebSiteData implements SiteData {
    @Override
    public String getSiteName() {
        return "루리웹";
    }

    @Override
    public String getDomain() {
        return "https://m.ruliweb.com";
    }

    @Override
    public String getBoardUrl() {
        return "/ps/board/1020";
    }
}

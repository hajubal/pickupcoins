package me.synology.hajubal.coins.crawler.impl.clien;

import me.synology.hajubal.coins.crawler.SiteData;
import org.springframework.stereotype.Component;

@Component
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

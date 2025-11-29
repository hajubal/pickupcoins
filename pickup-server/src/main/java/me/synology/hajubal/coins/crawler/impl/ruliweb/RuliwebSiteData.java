package me.synology.hajubal.coins.crawler.impl.ruliweb;

import me.synology.hajubal.coins.crawler.SiteData;
import org.springframework.stereotype.Component;

/**
 * 루리웹 사이트 데이터
 * Note: 향후 데이터베이스로 관리 예정
 */
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

package me.synology.hajubal.coins.crawler.impl.clien;

import me.synology.hajubal.coins.crawler.SiteData;
import org.springframework.stereotype.Component;


/**
 * 클리앙 사이트 데이터
 * Note: 향후 데이터베이스로 관리 예정
 */
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

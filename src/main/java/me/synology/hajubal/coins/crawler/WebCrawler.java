package me.synology.hajubal.coins.crawler;

import me.synology.hajubal.coins.entity.PointUrl;

import java.io.IOException;
import java.util.Set;

public interface WebCrawler {
    /**
     * 사이트에 포함된 포인트 url 수집
     */
    Set<PointUrl> crawling() throws IOException;

    String getSiteName();

    String getDomain();

    String getBoardUrl();
}

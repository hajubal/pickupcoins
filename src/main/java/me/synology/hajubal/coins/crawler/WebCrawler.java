package me.synology.hajubal.coins.crawler;

import java.util.Set;

public interface WebCrawler {
    /**
     * 사이트에 포함된 포인트 url 수집
     */
    Set<String> crawling();

}

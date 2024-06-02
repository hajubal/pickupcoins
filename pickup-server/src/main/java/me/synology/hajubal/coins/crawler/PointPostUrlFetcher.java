package me.synology.hajubal.coins.crawler;

import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class PointPostUrlFetcher {

    private final PointUrlSelector pointUrlSelector;

    /**
     * 포인트 적립 URL을 가지고 있는 게시물의 URL
     *
     * @param siteUrl 포인트 적립 게시물이 올라오는 게시판 URL
     * @return 포인트 적입 게시물들
     */
    public Set<String> fetchPostUrls(String siteUrl) throws IOException {
        //게시판 목록 tag
        return Jsoup.connect(siteUrl).get().select(pointUrlSelector.titleCssQuery())
                .stream()
                .filter(pointUrlSelector::titleSelector)
                .map(pointUrlSelector::linkExtractor)
                .collect(Collectors.toSet());
    }

}

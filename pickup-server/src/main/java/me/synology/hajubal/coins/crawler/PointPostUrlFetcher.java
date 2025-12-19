package me.synology.hajubal.coins.crawler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class PointPostUrlFetcher {

    private final PointUrlSelector pointUrlSelector;

    /**
     * 포인트 적립 URL을 가지고 있는 게시물의 URL
     *
     * @param siteUrl 포인트 적립 게시물이 올라오는 게시판 URL
     * @return 포인트 적입 게시물들
     */
    public Set<String> fetchPostUrls(String siteUrl) throws IOException {
        String cssQuery = pointUrlSelector.titleCssQuery();
        log.debug("크롤링 시작 - siteUrl: {}, cssQuery: {}", siteUrl, cssQuery);

        Elements selectedElements = Jsoup.connect(siteUrl).get().select(cssQuery);
        log.debug("CSS 선택자로 찾은 요소 개수: {}", selectedElements.size());

        if (log.isTraceEnabled()) {
            for (Element element : selectedElements) {
                log.trace("선택된 요소 - text: {}, href: {}", element.text(), element.attr("href"));
            }
        }

        Set<String> result = selectedElements.stream()
                .filter(element -> {
                    boolean matched = pointUrlSelector.titleSelector(element);
                    if (matched) {
                        log.debug("필터 통과 - text: {}", element.text());
                    }
                    return matched;
                })
                .map(pointUrlSelector::linkExtractor)
                .collect(Collectors.toSet());

        log.info("크롤링 완료 - siteUrl: {}, 수집된 URL 개수: {}", siteUrl, result.size());
        if (log.isDebugEnabled()) {
            result.forEach(url -> log.debug("수집된 URL: {}", url));
        }

        return result;
    }

}

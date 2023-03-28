package me.synology.hajubal.coins.crawler.impl.ruliweb;

import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.crawler.PointPostUrlFetcher;
import me.synology.hajubal.coins.crawler.WebCrawler;
import me.synology.hajubal.coins.entity.PointUrl;
import me.synology.hajubal.coins.entity.Site;
import me.synology.hajubal.coins.entity.type.POINT_URL_TYPE;
import me.synology.hajubal.coins.respository.SiteRepository;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * 루리웹 사이트 핫딜 게시판
 */
@Slf4j
@Component
public class RuliwebCrawler implements WebCrawler {

    private final PointPostUrlFetcher pointPostUrlFetcher;

    private final SiteRepository siteRepository;

    public RuliwebCrawler(RuliwebPointUrlSelector ruliwebPointUrlSelector, SiteRepository siteRepository) {
        this.pointPostUrlFetcher = new PointPostUrlFetcher(ruliwebPointUrlSelector);
        this.siteRepository = siteRepository;
    }

    /**
     * 사이트에 포함된 포인트 url 수집
     *
     * @return point url set
     */
    @Transactional
    @Override
    public Set<PointUrl> crawling() throws IOException {
        Optional<Site> optionalSite = siteRepository.findByName("루리웹");

        if(optionalSite.isEmpty()) return Collections.emptySet();

        Site site = optionalSite.get();

        return extractPointUrls(site.getDomain(), pointPostUrlFetcher.fetchPostUrls(site.getUrl()));
    }

    /**
     * 게시글 목록에서 네이버 포인트 url 조회
     *
     * @param domain
     * @param pointPostUrl
     * @return
     */
    private Set<PointUrl> extractPointUrls(String domain, Set<String> pointPostUrl) throws IOException {
        //포인트 url
        Set<PointUrl> pointUrl = new HashSet<>();

        for (String postUrl : pointPostUrl) {
            //게시글 내용에 링크
            Jsoup.connect(domain + postUrl).get().select(".board_main_view a").forEach(aTag -> {
                String pointHref = aTag.attr("href");

                log.debug("Ruliweb point url: {}", pointHref);

                POINT_URL_TYPE pointType = POINT_URL_TYPE.classifyUrlType(pointHref);

                pointUrl.add(PointUrl.builder()
                        .pointUrlType(pointType)
                        .url(pointHref)
                        .name(pointType.name())
                        .build());
            });
        }

        return pointUrl;
    }

}

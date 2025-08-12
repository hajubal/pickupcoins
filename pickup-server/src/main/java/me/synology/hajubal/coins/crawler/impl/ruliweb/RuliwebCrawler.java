package me.synology.hajubal.coins.crawler.impl.ruliweb;

import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.crawler.PointPostUrlFetcher;
import me.synology.hajubal.coins.crawler.WebCrawler;
import me.synology.hajubal.coins.entity.PointUrl;
import me.synology.hajubal.coins.entity.Site;
import me.synology.hajubal.coins.entity.type.POINT_URL_TYPE;
import me.synology.hajubal.coins.respository.SiteRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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

    private final RuliwebSiteData ruliwebSiteData;

    public RuliwebCrawler(PointPostUrlFetcher pointPostUrlFetcher, SiteRepository siteRepository,
                          RuliwebSiteData ruliwebSiteData) {
        this.pointPostUrlFetcher = pointPostUrlFetcher;
        this.siteRepository = siteRepository;
        this.ruliwebSiteData = ruliwebSiteData;
    }

    /**
     * 사이트에 포함된 포인트 url 수집
     *
     * @return point url set
     */
    @Transactional
    @Override
    public Set<PointUrl> crawling() throws IOException {
        Optional<Site> optionalSite = siteRepository.findByName(ruliwebSiteData.getSiteName());

        if(optionalSite.isEmpty()) return Collections.emptySet();

        Site site = optionalSite.get();

        return extractPointUrls(site.getDomain(), pointPostUrlFetcher.fetchPostUrls(site.getUrl()));
    }

    /**
     * 게시글 목록에서 네이버 포인트 url 조회
     *
     * @param domain
     * @param pointPostUrls
     * @return
     */
    private Set<PointUrl> extractPointUrls(String domain, Set<String> pointPostUrls) throws IOException {
        //포인트 url
        Set<PointUrl> pointUrls = new HashSet<>();

        for (String url : pointPostUrls) {
            //게시글 내용에 링크
            Elements articleElements = Jsoup.connect(domain + url).get().select(".board_main_view a");

            for (Element element : articleElements) {
                String href = element.attr("href");

                log.debug("Ruliweb point url: {}", href);

                pointUrls.add(PointUrl.builder()
                        .url(href)
                        .build());
            }
        }

        return pointUrls;
    }

}

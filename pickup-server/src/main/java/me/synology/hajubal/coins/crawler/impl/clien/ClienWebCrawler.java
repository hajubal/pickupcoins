package me.synology.hajubal.coins.crawler.impl.clien;

import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.crawler.PointPostUrlFetcher;
import me.synology.hajubal.coins.crawler.SiteData;
import me.synology.hajubal.coins.crawler.WebCrawler;
import me.synology.hajubal.coins.entity.PointUrl;
import me.synology.hajubal.coins.entity.Site;
import me.synology.hajubal.coins.entity.type.POINT_URL_TYPE;
import me.synology.hajubal.coins.respository.SiteRepository;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.jsoup.nodes.Element;

/**
 * 클리앙 사이트 포인트 url 크롤러
 */
@Slf4j
@Component
public class ClienWebCrawler implements WebCrawler {

    private final PointPostUrlFetcher pointPostUrlFetcher;

    private final SiteRepository siteRepository;

    private final ClientSiteData clientSiteData;

    public ClienWebCrawler(ClienPointUrlSelector clienPointUrlSelector, SiteRepository siteRepository
            , ClientSiteData clientSiteData) {
        this.pointPostUrlFetcher = new PointPostUrlFetcher(clienPointUrlSelector);
        this.siteRepository = siteRepository;
        this.clientSiteData = clientSiteData;
    }

    /**
     * 사이트에 포함된 포인트 url 수집
     *
     * @return point url set
     */
    @Transactional
    @Override
    public Set<PointUrl> crawling() throws IOException {
        Optional<Site> optionalSite = siteRepository.findByName(clientSiteData.getSiteName());

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
            Elements articleElements = Jsoup.connect(domain + url).get().select("div.post_article a");

            for (Element element : articleElements) {
                String href = element.attr("href");

                log.debug("Naver point url: {}", href);

                pointUrls.add(PointUrl.builder()
                        .url(href)
                        .build());
            }
        }

        return pointUrls;
    }

}

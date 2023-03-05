package me.synology.hajubal.coins.crawler.impl;

import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.crawler.WebCrawler;
import me.synology.hajubal.coins.entity.PointUrl;
import me.synology.hajubal.coins.entity.Site;
import me.synology.hajubal.coins.entity.type.POINT_URL_TYPE;
import me.synology.hajubal.coins.respository.SiteRepository;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Component
public class ClienWebCrawler implements WebCrawler {

    @Autowired
    private SiteRepository siteRepository;

    /**
     * 사이트에 포함된 포인트 url 수집
     *
     * @return point url set
     */
    @Transactional
    @Override
    public Set<PointUrl> crawling() throws IOException {
        Optional<Site> optionalSite = siteRepository.findByName("클리앙");

        if(optionalSite.isEmpty()) return Collections.emptySet();

        Site site = optionalSite.get();

        return fetchPointUrls(site.getDomain(), fetchPostUrls(site.getUrl()));
    }

    /**
     * 게시글 목록에서 네이버 포인트 url 조회
     *
     * @param domain
     * @param pointPostUrl
     * @return
     */
    @NotNull
    private static Set<PointUrl> fetchPointUrls(String domain, Set<String> pointPostUrl) throws IOException {
        //포인트 url
        Set<PointUrl> pointUrl = new HashSet<>();

        for (String postUrl : pointPostUrl) {
            //게시글 내용에 링크
            Jsoup.connect(domain + postUrl).get().select("div.post_article a").forEach(aTag -> {
                String pointHref = aTag.attr("href");

                if(pointHref.contains("campaign2-api.naver.com")) {
                    log.debug("Naver point url: {}", pointHref);

                    pointUrl.add(PointUrl.builder().pointUrlType(POINT_URL_TYPE.NAVER).url(pointHref).name("naver").build());
                } else if(pointHref.contains("ofw.adison.co/u/naverpay")) {
                    log.debug("Adison naver point url: {}", pointHref);

                    pointUrl.add(PointUrl.builder().pointUrlType(POINT_URL_TYPE.OFW_NAVER).url(pointHref).name("adison").build());
                }
            });
        }

        return pointUrl;
    }

    /**
     * 포인트 적립 URL을 가지고 있는 게시물의 URL
     *
     * @param siteUrl
     * @return
     * @throws IOException
     */
    @NotNull
    private static Set<String> fetchPostUrls(String siteUrl) throws IOException {
        Set<String> pointPostUrl = new HashSet<>();

        //게시판 목록 tag
        Jsoup.connect(siteUrl).get().select("span.list_subject").forEach(element -> {
            if(element.attr("title").contains("네이버")) {
                pointPostUrl.add(element.select("a[data-role=list-title-text]").attr("href"));
            }
        });
        return pointPostUrl;
    }

    @Override
    public String siteName() {
        return "Clien";
    }
}

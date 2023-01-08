package me.synology.hajubal.coins.crawler.impl;

import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.crawler.WebCrawler;
import me.synology.hajubal.coins.entity.POINT_URL_TYPE;
import me.synology.hajubal.coins.entity.PointUrl;
import me.synology.hajubal.coins.entity.Site;
import me.synology.hajubal.coins.respository.SiteRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashSet;
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
    public Set<PointUrl> crawling() {
        Site site = siteRepository.findByName("클리앙").orElseThrow(() -> new IllegalArgumentException("Not found site name. 클리앙"));

        String siteUrl = site.getUrl();

        //포인트 적립 URL을 가지고 있는 게시물의 URL
        Set<String> pointPostUrl = new HashSet<>();

        Document document;

        try {
            document = Jsoup.connect(siteUrl).get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //게시판 목록 tag
        document.select("span.list_subject").forEach(element -> {
            //게시글 제목
            String title = element.attr("title");

            if(title.contains("네이버")) {
                //게시글 상세 링크
                String href = element.select("a[data-role=list-title-text]").attr("href");

                log.debug("add url: {}", href);

                pointPostUrl.add(href);
            }
        });

        //포인트 url
        Set<PointUrl> pointUrl = new HashSet<>();

        pointPostUrl.forEach(url -> {
            Document postDocument;

            try {
                postDocument = Jsoup.connect(site.getDomain() + url).get();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            //게시글 내용에 링크
            postDocument.select("div.post_article a").forEach(aTag -> {
                String pointHref = aTag.attr("href");

                if(pointHref.contains("campaign2-api.naver.com")) {
                    log.debug("Naver point url: {}", pointHref);

                    pointUrl.add(PointUrl.builder().pointUrlType(POINT_URL_TYPE.NAVER).url(pointHref).name("naver").build());
                } else if(pointHref.contains("ofw.adison.co/u/naverpay")) {
                    log.debug("Adison naver point url: {}", pointHref);

                    pointUrl.add(PointUrl.builder().pointUrlType(POINT_URL_TYPE.OFW_NAVER).url(pointHref).name("naver").build());
                }
            });
        });

        return pointUrl;
    }

    @Override
    public String siteName() {
        return "Clien";
    }
}

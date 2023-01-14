package me.synology.hajubal.coins.crawler.impl;

import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.crawler.WebCrawler;
import me.synology.hajubal.coins.entity.type.POINT_URL_TYPE;
import me.synology.hajubal.coins.entity.PointUrl;
import me.synology.hajubal.coins.entity.Site;
import me.synology.hajubal.coins.respository.SiteRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * 루리웹 사이트 핫딜 게시판
 */
@Slf4j
@Component
public class RuliwebCrawler implements WebCrawler {

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
        Site site = siteRepository.findByName("루리웹").orElseThrow(() -> new IllegalArgumentException("Not found site name. 루리웹"));

        Document document = null;

        try {
            document = Jsoup.connect(site.getUrl()).get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Set<String> pointPostUrl = new HashSet<>();

        Elements select = document.select("div.list_content>a.subject_link.deco");

        select.forEach(action -> {
            String text = action.text();

            if(text.contains("네이버")) {
                String href = action.attr("href");

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
            postDocument.select(".board_main_view a").forEach(aTag -> {
                String pointHref = aTag.attr("href");

                if(pointHref.contains("campaign2-api.naver.com")) {
                    log.debug("Naver point url: {}", pointHref);

                    pointUrl.add(PointUrl.builder().pointUrlType(POINT_URL_TYPE.NAVER).url(pointHref).name("ruliweb").build());
                } else if(pointHref.contains("ofw.adison.co/u/naverpay")) {
                    log.debug("Adison naver point url: {}", pointHref);

                    pointUrl.add(PointUrl.builder().pointUrlType(POINT_URL_TYPE.OFW_NAVER).url(pointHref).name("ruliweb").build());
                }
            });
        });

        return pointUrl;
    }

    @Override
    public String siteName() {
        return "Ruliweb";
    }
}

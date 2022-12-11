package me.synology.hajubal.coins.service;

import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.entity.PointUrl;
import me.synology.hajubal.coins.entity.Site;
import me.synology.hajubal.coins.respository.PointUrlRepository;
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
public class RuliwebCrawer implements WebCrawler {

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private PointUrlRepository pointUrlRepository;

    @Transactional
    @Override
    public void crawling() {
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
        Set<String> pointUrl = new HashSet<>();

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
                    log.debug("point url: {}", pointHref);

                    pointUrl.add(pointHref);
                }
            });
        });

        pointUrl.forEach(url -> {
            if(pointUrlRepository.findByUrl(url).isEmpty()) {
                log.info("save point url: {}", url);

                pointUrlRepository.save(PointUrl.builder()
                        .url(url)
                        .name(url)
                        .build());
            }
        });
    }
}

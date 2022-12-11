package me.synology.hajubal.coins.service;

import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.entity.PointUrl;
import me.synology.hajubal.coins.entity.Site;
import me.synology.hajubal.coins.respository.PointUrlRepository;
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

    @Autowired
    private PointUrlRepository pointUrlRepository;

    @Autowired
    private SlackService sendMessage;

    /**
     * 사이트에 포함된 포인트 url 수집
     */
    @Transactional
    @Override
    public void crawling() {
        Site site = siteRepository.findByName("클리앙").orElseThrow(() -> new IllegalArgumentException("Not found site name. 클리앙"));

        String siteUrl = site.getUrl();

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

//            log.info("title: {}", title);

            if(title.contains("네이버")) {
                //게시글 상세 링크
                String href = element.select("a[data-role=list-title-text]").attr("href");

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
            postDocument.select("div.post_article a").forEach(aTag -> {
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

package me.synology.hajubal.coins.service;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class WebCrawlerTest {

    @Test
    void crawl() throws Exception {

        Set<String> pointPostUrl = new HashSet<>();

        Document document = Jsoup.connect("https://www.clien.net/service/board/jirum").get();

        //게시판 목록 tag
        document.select("span.list_subject").forEach(element -> {
            //게시글 제목
            String title = element.attr("title");

            log.info("title: {}", title);

            if(title.contains("네이버")) {
                //게시글 상세 링크
                String href = element.select("a[data-role=list-title-text]").attr("href");

                log.info("add url: {}", href);

                pointPostUrl.add(href);
            }
        });

        Set<String> pointUrl = new HashSet<>();

        pointPostUrl.forEach(url -> {
            Document postDocument;

            try {
                postDocument = Jsoup.connect("https://www.clien.net" + url).get();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            //게시글 내용에 링크
            postDocument.select("div.post_article a").forEach(aTag -> {
                String pointHref = aTag.attr("href");

                if(pointHref.contains("campaign2-api.naver.com")) {
                    pointUrl.add(pointHref);
                }
            });
        });

        pointUrl.forEach(System.out::println);
    }
}

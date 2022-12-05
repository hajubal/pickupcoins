package me.synology.hajubal.coins.service;

import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.entity.SiteData;
import me.synology.hajubal.coins.respository.SiteRepository;
import me.synology.hajubal.coins.respository.PointUrlRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class WebCrawler {

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private PointUrlRepository pointUrlRepository;

    @Autowired
    private SlackService sendMessage;

    /**
     * 사이트에 포함된 포인트 url 수집
     */
    public void crawl() {
        List<SiteData> siteDataList = siteRepository.findAll();

        siteDataList.forEach(site -> {
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
                    postDocument = Jsoup.connect(site.getDomain() + url).get();
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


        });
    }

    List<String> outOfStackList = new ArrayList<>();

    /**
     * 루리웹 알뜰 게시판에 충전거치대 매물 확인
     *
     * @throws Exception
     */
    public void checkRuliweb() throws IOException {
        String url = "https://m.ruliweb.com/ps/board/1020";

        Document document = Jsoup.connect(url).get();

        Elements select = document.select("div.list_content>a.subject_link.deco");

        select.forEach(action -> {
            String text = action.text();

            if((text.contains("충전거치대") || text.contains("충전 거치대")) && !text.contains("품절")) {
                String attr = action.attr("href");

                if(this.outOfStackList.contains(attr) == false) {
                    this.outOfStackList.add(attr);

                    try {
                        sendMessage.sendMessage("매물 등장!!! " + url);
                    } catch (IOException e) {
                        log.error(e.getMessage(), e.getCause());
                    }

                    return;
                }
            }
        });
    }

    /**
     * 파트너샵에 충전거지대 매물 확인
     *
     * @throws Exception
     */
    public void checkPartenerShop() throws IOException {
        String url = "https://partnershopplus.com/shop/item.php?it_id=1617693034";

        Document document = Jsoup.connect(url).get();

        Elements elements = document.select("p#sit_ov_soldout");

        if(elements.size() == 0) {
            sendMessage.sendMessage("매물 등장!!! " + url);
        }
    }
}

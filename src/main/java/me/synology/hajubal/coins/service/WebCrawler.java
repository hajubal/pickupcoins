package me.synology.hajubal.coins.service;

import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.entity.SiteData;
import me.synology.hajubal.coins.respository.SiteRepository;
import me.synology.hajubal.coins.respository.UrlRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class WebCrawler {

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private UrlRepository urlRepository;

    @Autowired
    private SlackService sendMessage;

    public void crawl() {
        List<SiteData> siteDataList = siteRepository.findAll();

        siteDataList.forEach(site -> {
            String url = site.getUrl();

            try {
                Document document = Jsoup.connect(url).get();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
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

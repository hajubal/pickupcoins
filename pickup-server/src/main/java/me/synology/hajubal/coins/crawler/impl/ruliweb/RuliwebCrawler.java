package me.synology.hajubal.coins.crawler.impl.ruliweb;

import java.io.IOException;
import java.util.Set;
import me.synology.hajubal.coins.config.CrawlerProperties;
import me.synology.hajubal.coins.crawler.AbstractWebCrawler;
import me.synology.hajubal.coins.crawler.PointPostUrlFetcher;
import me.synology.hajubal.coins.crawler.SiteData;
import me.synology.hajubal.coins.respository.SiteRepository;
import org.springframework.stereotype.Component;

/**
 * 루리웹 사이트 핫딜 게시판 포인트 URL 크롤러
 */
@Component
public class RuliwebCrawler extends AbstractWebCrawler {

  private final PointPostUrlFetcher pointPostUrlFetcher;
  private final RuliwebSiteData ruliwebSiteData;

  public RuliwebCrawler(
      PointPostUrlFetcher pointPostUrlFetcher,
      SiteRepository siteRepository,
      CrawlerProperties crawlerProperties,
      RuliwebSiteData ruliwebSiteData) {
    super(siteRepository, crawlerProperties);
    this.pointPostUrlFetcher = pointPostUrlFetcher;
    this.ruliwebSiteData = ruliwebSiteData;
  }

  @Override
  protected SiteData getSiteData() {
    return ruliwebSiteData;
  }

  @Override
  protected String getArticleSelector() {
    return ".board_main_view a";
  }

  @Override
  protected Set<String> fetchPostUrls(String siteUrl) throws IOException {
    return pointPostUrlFetcher.fetchPostUrls(siteUrl);
  }
}

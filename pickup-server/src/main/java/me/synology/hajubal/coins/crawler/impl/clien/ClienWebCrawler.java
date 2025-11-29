package me.synology.hajubal.coins.crawler.impl.clien;

import java.io.IOException;
import java.util.Set;
import me.synology.hajubal.coins.config.CrawlerProperties;
import me.synology.hajubal.coins.crawler.AbstractWebCrawler;
import me.synology.hajubal.coins.crawler.PointPostUrlFetcher;
import me.synology.hajubal.coins.crawler.SiteData;
import me.synology.hajubal.coins.respository.SiteRepository;
import org.springframework.stereotype.Component;

/**
 * 클리앙 사이트 포인트 URL 크롤러
 */
@Component
public class ClienWebCrawler extends AbstractWebCrawler {

  private final PointPostUrlFetcher pointPostUrlFetcher;
  private final ClientSiteData clientSiteData;

  public ClienWebCrawler(
      PointPostUrlFetcher pointPostUrlFetcher,
      SiteRepository siteRepository,
      CrawlerProperties crawlerProperties,
      ClientSiteData clientSiteData) {
    super(siteRepository, crawlerProperties);
    this.pointPostUrlFetcher = pointPostUrlFetcher;
    this.clientSiteData = clientSiteData;
  }

  @Override
  protected SiteData getSiteData() {
    return clientSiteData;
  }

  @Override
  protected String getArticleSelector() {
    return "div.post_article a";
  }

  @Override
  protected Set<String> fetchPostUrls(String siteUrl) throws IOException {
    return pointPostUrlFetcher.fetchPostUrls(siteUrl);
  }
}

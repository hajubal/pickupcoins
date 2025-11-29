package me.synology.hajubal.coins.crawler;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.config.CrawlerProperties;
import me.synology.hajubal.coins.entity.PointUrl;
import me.synology.hajubal.coins.entity.Site;
import me.synology.hajubal.coins.respository.SiteRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.transaction.annotation.Transactional;

/**
 * 웹 크롤러 추상 클래스
 * 공통 크롤링 로직을 제공하고, 사이트별로 다른 부분만 하위 클래스에서 구현
 */
@Slf4j
public abstract class AbstractWebCrawler implements WebCrawler {

  protected final SiteRepository siteRepository;
  protected final CrawlerProperties crawlerProperties;

  protected AbstractWebCrawler(
      SiteRepository siteRepository, CrawlerProperties crawlerProperties) {
    this.siteRepository = siteRepository;
    this.crawlerProperties = crawlerProperties;
  }

  /**
   * 사이트 데이터 반환 (하위 클래스에서 구현)
   */
  protected abstract SiteData getSiteData();

  /**
   * 게시글 본문에서 링크를 추출할 CSS 셀렉터 반환 (하위 클래스에서 구현)
   *
   * @return CSS 셀렉터 (예: "div.post_article a", ".board_main_view a")
   */
  protected abstract String getArticleSelector();

  /**
   * 포인트 URL을 포함한 게시글 URL 목록 조회 (하위 클래스에서 구현)
   *
   * @param siteUrl 사이트 URL
   * @return 게시글 URL 집합
   */
  protected abstract Set<String> fetchPostUrls(String siteUrl) throws IOException;

  /**
   * 사이트에 포함된 포인트 URL 수집
   *
   * @return 포인트 URL 집합
   */
  @Transactional
  @Override
  public Set<PointUrl> crawling() throws IOException {
    SiteData siteData = getSiteData();
    Site site =
        siteRepository
            .findByName(siteData.getSiteName())
            .orElseGet(
                () -> {
                  log.warn("Site not found: {}", siteData.getSiteName());
                  return null;
                });

    if (site == null) {
      return Collections.emptySet();
    }

    Set<String> postUrls = fetchPostUrls(site.getUrl());
    Set<PointUrl> pointUrls = extractPointUrls(site.getDomain(), postUrls);

    log.info(
        "Crawling completed. site: {}, posts: {}, points: {}",
        siteData.getSiteName(),
        postUrls.size(),
        pointUrls.size());

    return pointUrls;
  }

  /**
   * 게시글 목록에서 네이버 포인트 URL 추출
   *
   * @param domain 사이트 도메인
   * @param postUrls 게시글 URL 목록
   * @return 추출된 포인트 URL 집합
   */
  private Set<PointUrl> extractPointUrls(String domain, Set<String> postUrls) throws IOException {
    Set<PointUrl> pointUrls = new HashSet<>();

    for (String url : postUrls) {
      try {
        // 게시글 본문에서 링크 추출
        Elements articleElements =
            Jsoup.connect(domain + url)
                .timeout(crawlerProperties.getTimeout())
                .get()
                .select(getArticleSelector());

        for (Element element : articleElements) {
          String href = element.attr("href");

          if (isNaverPointUrl(href)) {
            log.debug("Found point URL: {}", href);
            pointUrls.add(PointUrl.builder().url(href).build());
          }
        }
      } catch (IOException e) {
        log.warn("Failed to crawl URL: {}. Error: {}", domain + url, e.getMessage());
        // 개별 URL 실패는 전체 크롤링을 중단하지 않음
      }
    }

    return pointUrls;
  }

  /**
   * 네이버 포인트 URL인지 확인
   *
   * @param url URL 문자열
   * @return 네이버 포인트 URL이면 true
   */
  private boolean isNaverPointUrl(String url) {
    return url != null
        && (url.contains("naver.com/point")
            || url.contains("naver.me")
            || url.contains("m.site.naver.com"));
  }
}

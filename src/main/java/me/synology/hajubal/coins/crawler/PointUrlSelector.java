package me.synology.hajubal.coins.crawler;

import org.jsoup.nodes.Element;

public interface PointUrlSelector {

    /**
     * 게시글 링크 추출
     *
     * @param element
     * @return
     */
    String linkExtractor(Element element);

    /**
     * 제목인지 여부
     *
     * @param element
     * @return
     */
    boolean titleSelector(Element element);

    /**
     * 게시글 제목을 가지고 있는 element css query
     * @return
     */
    String titleCssQuery();
}

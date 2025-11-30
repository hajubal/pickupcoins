package me.synology.hajubal.coins.crawler.impl.clien;

import me.synology.hajubal.coins.crawler.PointUrlSelector;
import org.jsoup.nodes.Element;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
public class ClienPointUrlSelector implements PointUrlSelector {
    @Override
    public String linkExtractor(Element element) {
        return element.select("a[data-role=list-title-text]").attr("href");
    }

    @Override
    public boolean titleSelector(Element element) {
        return element.attr("title").contains("네이버");
    }

    @Override
    public String titleCssQuery() {
        return "span.list_subject";
    }

}

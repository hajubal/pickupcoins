package me.synology.hajubal.coins.crawler.impl.ruliweb;

import me.synology.hajubal.coins.crawler.PointUrlSelector;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

@Component
public class RuliwebPointUrlSelector implements PointUrlSelector {
    @Override
    public String linkExtractor(Element element) {
        return element.attr("href");
    }

    @Override
    public boolean titleSelector(Element element) {
        return element.text().contains("네이버");
    }

    @Override
    public String titleCssQuery() {
        return "a.subject_link.deco";
    }

}

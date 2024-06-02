package me.synology.hajubal.coins;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.crawler.SiteData;
import me.synology.hajubal.coins.entity.Site;
import me.synology.hajubal.coins.respository.SiteRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 시스템 초기화 데이터 저장
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class DataInitializer implements ApplicationRunner {

    private final SiteRepository siteRepository;

    /**
     * site data beans
     */
    private final List<SiteData> siteData;

    @Transactional
    @Override
    public void run(ApplicationArguments args) {
        log.info("Initialize user info.");

        siteData.forEach(data -> {
            if (siteRepository.findByName(data.getSiteName()).isEmpty()) {
                siteRepository.save(
                        Site.builder()
                        .name(data.getSiteName())
                        .domain(data.getDomain())
                        .url(data.getDomain() + data.getBoardUrl())
                        .build()
                );
            }
        });
    }
}

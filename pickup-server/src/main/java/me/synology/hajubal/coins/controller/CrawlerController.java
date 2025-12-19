package me.synology.hajubal.coins.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.service.WebCrawlerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/crawler")
@RequiredArgsConstructor
public class CrawlerController {

    private final WebCrawlerService webCrawlerService;

    @PostMapping("/trigger")
    public ResponseEntity<Map<String, Object>> triggerCrawling() {
        log.info("Manual crawling triggered via REST API");

        try {
            long startTime = System.currentTimeMillis();
            webCrawlerService.savingPointUrl();
            long duration = System.currentTimeMillis() - startTime;

            log.info("Manual crawling completed in {}ms", duration);

            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Crawling completed successfully",
                "durationMs", duration
            ));
        } catch (Exception e) {
            log.error("Manual crawling failed", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "status", "error",
                "message", "Crawling failed: " + e.getMessage()
            ));
        }
    }
}

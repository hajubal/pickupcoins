package me.synology.hajubal.coins.admin.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.admin.config.PickupServerProperties;
import me.synology.hajubal.coins.admin.dto.CrawlTriggerResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RestController
@RequestMapping("/api/v1/crawler")
@RequiredArgsConstructor
public class CrawlerController {

    private final RestTemplate restTemplate;
    private final PickupServerProperties pickupServerProperties;

    @PostMapping("/trigger")
    public ResponseEntity<CrawlTriggerResponse> triggerCrawling() {
        log.info("Proxying crawl trigger request to pickup-server");

        String url = pickupServerProperties.getBaseUrl() + "/api/v1/crawler/trigger";

        try {
            ResponseEntity<CrawlTriggerResponse> response = restTemplate.postForEntity(
                url,
                null,
                CrawlTriggerResponse.class
            );

            log.info("Crawl trigger response: {}", response.getStatusCode());
            return ResponseEntity.ok(response.getBody());

        } catch (Exception e) {
            log.error("Failed to trigger crawling on pickup-server", e);
            return ResponseEntity.internalServerError().body(
                CrawlTriggerResponse.builder()
                    .status("error")
                    .message("Failed to connect to pickup-server: " + e.getMessage())
                    .build()
            );
        }
    }
}

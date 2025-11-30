package me.synology.hajubal.coins.admin.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.admin.dto.SiteDto;
import me.synology.hajubal.coins.admin.dto.SiteRequest;
import me.synology.hajubal.coins.entity.Site;
import me.synology.hajubal.coins.respository.SiteRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1/sites")
@RequiredArgsConstructor
public class SiteController {

  private final SiteRepository siteRepository;

  @GetMapping
  public ResponseEntity<List<SiteDto>> getAllSites() {
    log.info("Getting all sites");
    List<Site> sites = siteRepository.findAll();
    List<SiteDto> siteDtos = sites.stream().map(SiteDto::from).collect(Collectors.toList());
    return ResponseEntity.ok(siteDtos);
  }

  @GetMapping("/{id}")
  public ResponseEntity<SiteDto> getSite(@PathVariable Long id) {
    log.info("Getting site: {}", id);
    return siteRepository
        .findById(id)
        .map(SiteDto::from)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping
  public ResponseEntity<SiteDto> createSite(@Valid @RequestBody SiteRequest request) {
    log.info("Creating site: {}", request.getName());

    Site site =
        Site.builder()
            .name(request.getName())
            .domain(request.getDomain())
            .url(request.getUrl())
            .build();

    Site savedSite = siteRepository.save(site);
    log.info("Site created successfully: {}", savedSite.getId());

    return ResponseEntity.ok(SiteDto.from(savedSite));
  }

  @PutMapping("/{id}")
  public ResponseEntity<SiteDto> updateSite(
      @PathVariable Long id, @Valid @RequestBody SiteRequest request) {
    log.info("Updating site: {}", id);

    return siteRepository
        .findById(id)
        .map(
            site -> {
              Site updated =
                  Site.builder()
                      .name(request.getName())
                      .domain(request.getDomain())
                      .url(request.getUrl())
                      .build();
              // Copy the ID to update existing entity
              try {
                java.lang.reflect.Field idField = Site.class.getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(updated, site.getId());
              } catch (Exception e) {
                log.error("Failed to set ID", e);
              }
              Site savedSite = siteRepository.save(updated);
              log.info("Site updated successfully: {}", id);
              return ResponseEntity.ok(SiteDto.from(savedSite));
            })
        .orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteSite(@PathVariable Long id) {
    log.info("Deleting site: {}", id);

    if (!siteRepository.existsById(id)) {
      return ResponseEntity.notFound().build();
    }

    siteRepository.deleteById(id);
    log.info("Site deleted successfully: {}", id);
    return ResponseEntity.noContent().build();
  }
}

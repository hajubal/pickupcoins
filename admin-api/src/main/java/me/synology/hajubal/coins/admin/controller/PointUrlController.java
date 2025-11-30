package me.synology.hajubal.coins.admin.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.admin.dto.PointUrlDto;
import me.synology.hajubal.coins.admin.dto.PointUrlRequest;
import me.synology.hajubal.coins.entity.PointUrl;
import me.synology.hajubal.coins.respository.PointUrlRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1/point-urls")
@RequiredArgsConstructor
public class PointUrlController {

  private final PointUrlRepository pointUrlRepository;

  @GetMapping
  public ResponseEntity<List<PointUrlDto>> getAllPointUrls() {
    log.info("Getting all point URLs");
    List<PointUrl> pointUrls = pointUrlRepository.findAll();
    List<PointUrlDto> pointUrlDtos =
        pointUrls.stream().map(PointUrlDto::from).collect(Collectors.toList());
    return ResponseEntity.ok(pointUrlDtos);
  }

  @GetMapping("/{id}")
  public ResponseEntity<PointUrlDto> getPointUrl(@PathVariable Long id) {
    log.info("Getting point URL: {}", id);
    return pointUrlRepository
        .findById(id)
        .map(PointUrlDto::from)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping
  public ResponseEntity<PointUrlDto> createPointUrl(@Valid @RequestBody PointUrlRequest request) {
    log.info("Creating point URL: {}", request.getUrl());

    PointUrl pointUrl =
        PointUrl.builder()
            .url(request.getUrl())
            .permanent(request.getPermanent() != null ? request.getPermanent() : false)
            .build();

    PointUrl savedPointUrl = pointUrlRepository.save(pointUrl);
    log.info("Point URL created successfully: {}", savedPointUrl.getId());

    return ResponseEntity.ok(PointUrlDto.from(savedPointUrl));
  }

  @PutMapping("/{id}")
  public ResponseEntity<PointUrlDto> updatePointUrl(
      @PathVariable Long id, @Valid @RequestBody PointUrlRequest request) {
    log.info("Updating point URL: {}", id);

    return pointUrlRepository
        .findById(id)
        .map(
            pointUrl -> {
              PointUrl updated =
                  PointUrl.builder()
                      .url(request.getUrl())
                      .permanent(request.getPermanent() != null ? request.getPermanent() : false)
                      .build();
              // Copy the ID to update existing entity
              try {
                java.lang.reflect.Field idField = PointUrl.class.getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(updated, pointUrl.getId());
              } catch (Exception e) {
                log.error("Failed to set ID", e);
              }
              PointUrl savedPointUrl = pointUrlRepository.save(updated);
              log.info("Point URL updated successfully: {}", id);
              return ResponseEntity.ok(PointUrlDto.from(savedPointUrl));
            })
        .orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deletePointUrl(@PathVariable Long id) {
    log.info("Deleting point URL: {}", id);

    if (!pointUrlRepository.existsById(id)) {
      return ResponseEntity.notFound().build();
    }

    pointUrlRepository.deleteById(id);
    log.info("Point URL deleted successfully: {}", id);
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/{id}/toggle-permanent")
  public ResponseEntity<PointUrlDto> togglePermanent(@PathVariable Long id) {
    log.info("Toggling permanent status for point URL: {}", id);

    return pointUrlRepository
        .findById(id)
        .map(
            pointUrl -> {
              PointUrl updated =
                  PointUrl.builder()
                      .url(pointUrl.getUrl())
                      .permanent(!Boolean.TRUE.equals(pointUrl.getPermanent()))
                      .build();
              // Copy the ID to update existing entity
              try {
                java.lang.reflect.Field idField = PointUrl.class.getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(updated, pointUrl.getId());
              } catch (Exception e) {
                log.error("Failed to set ID", e);
              }
              PointUrl savedPointUrl = pointUrlRepository.save(updated);
              log.info(
                  "Point URL permanent status toggled: {} -> {}",
                  id,
                  savedPointUrl.getPermanent());
              return ResponseEntity.ok(PointUrlDto.from(savedPointUrl));
            })
        .orElse(ResponseEntity.notFound().build());
  }
}

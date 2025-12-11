package me.synology.hajubal.coins.admin.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.admin.dto.SavedPointDto;
import me.synology.hajubal.coins.entity.SavedPoint;
import me.synology.hajubal.coins.respository.SavedPointRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1/point-logs")
@RequiredArgsConstructor
public class SavedPointController {

  private final SavedPointRepository savedPointRepository;

  @GetMapping
  public ResponseEntity<Map<String, Object>> getAllSavedPoints(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(required = false) String startDate,
      @RequestParam(required = false) String endDate) {
    log.info("Getting saved points: page={}, size={}, startDate={}, endDate={}", page, size, startDate, endDate);

    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));

    Page<SavedPoint> savedPointPage;

    if (startDate != null && endDate != null) {
      LocalDateTime start = LocalDate.parse(startDate).atStartOfDay();
      LocalDateTime end = LocalDate.parse(endDate).atTime(LocalTime.MAX);

      List<SavedPoint> filteredPoints = savedPointRepository.findAllByCreatedDateBetween(start, end);
      savedPointPage = new org.springframework.data.domain.PageImpl<>(
          filteredPoints.stream()
              .skip((long) page * size)
              .limit(size)
              .collect(Collectors.toList()),
          pageable,
          filteredPoints.size()
      );
    } else {
      savedPointPage = savedPointRepository.findAll(pageable);
    }

    List<SavedPointDto> savedPointDtos =
        savedPointPage.getContent().stream().map(SavedPointDto::from).collect(Collectors.toList());

    Map<String, Object> response = new HashMap<>();
    response.put("content", savedPointDtos);
    response.put("currentPage", savedPointPage.getNumber());
    response.put("totalItems", savedPointPage.getTotalElements());
    response.put("totalPages", savedPointPage.getTotalPages());

    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<SavedPointDto> getSavedPoint(@PathVariable Long id) {
    log.info("Getting saved point: {}", id);
    return savedPointRepository
        .findById(id)
        .map(SavedPointDto::from)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteSavedPoint(@PathVariable Long id) {
    log.info("Deleting saved point: {}", id);

    if (!savedPointRepository.existsById(id)) {
      return ResponseEntity.notFound().build();
    }

    savedPointRepository.deleteById(id);
    log.info("Saved point deleted successfully: {}", id);
    return ResponseEntity.noContent().build();
  }
}

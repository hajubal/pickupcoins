package me.synology.hajubal.coins.admin.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.admin.dto.DashboardStatsDto;
import me.synology.hajubal.coins.admin.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

  private final DashboardService dashboardService;

  @GetMapping("/stats")
  public ResponseEntity<DashboardStatsDto> getStats() {
    log.info("Getting dashboard statistics");
    DashboardStatsDto stats = dashboardService.getStats();
    return ResponseEntity.ok(stats);
  }
}

package me.synology.hajubal.coins.admin.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.admin.dto.CookieDto;
import me.synology.hajubal.coins.admin.dto.CookieRequest;
import me.synology.hajubal.coins.entity.Cookie;
import me.synology.hajubal.coins.entity.SiteUser;
import me.synology.hajubal.coins.respository.CookieRepository;
import me.synology.hajubal.coins.respository.SiteUserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1/cookies")
@RequiredArgsConstructor
public class CookieController {

  private final CookieRepository cookieRepository;
  private final SiteUserRepository siteUserRepository;

  @GetMapping
  public ResponseEntity<List<CookieDto>> getAllCookies() {
    log.info("Getting all cookies");
    List<Cookie> cookies = cookieRepository.findAll();
    List<CookieDto> cookieDtos =
        cookies.stream().map(CookieDto::from).collect(Collectors.toList());
    return ResponseEntity.ok(cookieDtos);
  }

  @GetMapping("/{id}")
  public ResponseEntity<CookieDto> getCookie(@PathVariable Long id) {
    log.info("Getting cookie: {}", id);
    return cookieRepository
        .findById(id)
        .map(CookieDto::from)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping
  public ResponseEntity<CookieDto> createCookie(
      @Valid @RequestBody CookieRequest request, Authentication authentication) {
    log.info("Creating cookie for site: {}", request.getSiteName());

    // Get current user
    SiteUser siteUser =
        siteUserRepository
            .findByLoginId(authentication.getName())
            .orElseThrow(() -> new RuntimeException("User not found"));

    Cookie cookie =
        Cookie.builder()
            .siteUser(siteUser)
            .userName(request.getUserName())
            .siteName(request.getSiteName())
            .cookie(request.getCookie())
            .isValid(request.getIsValid() != null ? request.getIsValid() : true)
            .build();

    Cookie savedCookie = cookieRepository.save(cookie);
    log.info("Cookie created successfully: {}", savedCookie.getId());

    return ResponseEntity.ok(CookieDto.from(savedCookie));
  }

  @PutMapping("/{id}")
  public ResponseEntity<CookieDto> updateCookie(
      @PathVariable Long id, @Valid @RequestBody CookieRequest request) {
    log.info("Updating cookie: {}", id);

    return cookieRepository
        .findById(id)
        .map(
            cookie -> {
              cookie.updateUserName(request.getUserName());
              cookie.updateSiteName(request.getSiteName());
              cookie.updateCookie(request.getCookie());
              if (request.getIsValid() != null) {
                if (request.getIsValid()) {
                  cookie.valid();
                } else {
                  cookie.invalid();
                }
              }
              Cookie updated = cookieRepository.save(cookie);
              log.info("Cookie updated successfully: {}", id);
              return ResponseEntity.ok(CookieDto.from(updated));
            })
        .orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteCookie(@PathVariable Long id) {
    log.info("Deleting cookie: {}", id);

    if (!cookieRepository.existsById(id)) {
      return ResponseEntity.notFound().build();
    }

    cookieRepository.deleteById(id);
    log.info("Cookie deleted successfully: {}", id);
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/{id}/toggle-validity")
  public ResponseEntity<CookieDto> toggleValidity(@PathVariable Long id) {
    log.info("Toggling validity for cookie: {}", id);

    return cookieRepository
        .findById(id)
        .map(
            cookie -> {
              if (Boolean.TRUE.equals(cookie.getIsValid())) {
                cookie.invalid();
              } else {
                cookie.valid();
              }
              Cookie updated = cookieRepository.save(cookie);
              log.info(
                  "Cookie validity toggled: {} -> {}", id, updated.getIsValid());
              return ResponseEntity.ok(CookieDto.from(updated));
            })
        .orElse(ResponseEntity.notFound().build());
  }
}

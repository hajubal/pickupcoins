package me.synology.hajubal.coins.service;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.entity.Cookie;
import me.synology.hajubal.coins.exception.CookieNotFoundException;
import me.synology.hajubal.coins.respository.CookieRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * 쿠키 서비스 기본 클래스
 * 공통 쿠키 관리 로직을 제공하고, 서버별로 다른 부분은 하위 클래스에서 구현
 */
@Slf4j
@Transactional(readOnly = true)
public abstract class BaseCookieService {

  protected final CookieRepository cookieRepository;

  protected BaseCookieService(CookieRepository cookieRepository) {
    this.cookieRepository = cookieRepository;
  }

  /**
   * 쿠키 조회
   *
   * @param cookieId 쿠키 ID
   * @return 쿠키 엔티티
   * @throws CookieNotFoundException 쿠키를 찾을 수 없는 경우
   */
  public Cookie getCookie(Long cookieId) {
    return cookieRepository
        .findById(cookieId)
        .orElseThrow(() -> new CookieNotFoundException(cookieId));
  }

  /**
   * 특정 사이트 사용자의 모든 쿠키 조회
   *
   * @param siteUserId 사이트 사용자 ID
   * @return 쿠키 목록
   */
  public List<Cookie> getAll(Long siteUserId) {
    return cookieRepository.findAllBySiteUser_Id(siteUserId);
  }

  /**
   * 쿠키 정보 업데이트 (쿠키 문자열만)
   *
   * @param cookieId 쿠키 ID
   * @param cookieStr 새로운 쿠키 문자열
   */
  @Transactional
  public void updateCookie(Long cookieId, String cookieStr) {
    log.debug("Updating cookie. cookieId: {}", cookieId);

    Cookie cookie = getCookie(cookieId);
    cookie.updateCookie(cookieStr);

    log.debug("Cookie updated successfully. user: {}", cookie.getUserName());
  }

  /**
   * 쿠키 무효화 처리
   * 하위 클래스에서 추가 처리(알림 등)가 필요한 경우 onCookieInvalidated() 메서드를 오버라이드
   *
   * @param cookieId 쿠키 ID
   */
  @Transactional
  public void invalid(Long cookieId) {
    Cookie cookie = getCookie(cookieId);
    cookie.invalid();

    log.warn("Cookie invalidated. user: {}", cookie.getUserName());

    onCookieInvalidated(cookie);
  }

  /**
   * 쿠키 무효화 후 추가 처리 (템플릿 메서드 패턴)
   * 하위 클래스에서 필요시 오버라이드하여 사용
   *
   * @param cookie 무효화된 쿠키
   */
  protected void onCookieInvalidated(Cookie cookie) {
    // 기본 구현은 아무것도 하지 않음
    // 하위 클래스에서 알림 전송 등의 추가 처리 구현
  }
}

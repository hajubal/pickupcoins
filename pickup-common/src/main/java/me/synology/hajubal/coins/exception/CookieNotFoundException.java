package me.synology.hajubal.coins.exception;

/**
 * 쿠키를 찾을 수 없을 때 발생하는 예외
 */
public class CookieNotFoundException extends RuntimeException {

  public CookieNotFoundException(Long cookieId) {
    super("Cookie not found: " + cookieId);
  }

  public CookieNotFoundException(String message) {
    super(message);
  }

  public CookieNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}

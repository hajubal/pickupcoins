package me.synology.hajubal.coins.exception;

/**
 * 유효하지 않은 쿠키일 때 발생하는 예외
 */
public class InvalidCookieException extends RuntimeException {

  public InvalidCookieException(String userName) {
    super("Invalid cookie for user: " + userName);
  }

  public InvalidCookieException(String message, Throwable cause) {
    super(message, cause);
  }
}

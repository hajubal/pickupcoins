package me.synology.hajubal.coins.exception;

/**
 * 포인트 교환 처리 중 발생하는 예외
 */
public class PointExchangeException extends RuntimeException {

  public PointExchangeException(String message) {
    super(message);
  }

  public PointExchangeException(String message, Throwable cause) {
    super(message, cause);
  }
}

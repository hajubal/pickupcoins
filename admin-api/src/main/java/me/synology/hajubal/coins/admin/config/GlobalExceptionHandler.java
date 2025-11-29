package me.synology.hajubal.coins.admin.config;

import jakarta.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.admin.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 전역 예외 처리 핸들러
 *
 * <p>모든 컨트롤러에서 발생하는 예외를 일관된 형식으로 처리합니다.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  /**
   * Validation 에러 처리
   *
   * @param ex MethodArgumentNotValidException
   * @param request HTTP 요청
   * @return 400 BAD_REQUEST 응답
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(
      MethodArgumentNotValidException ex, HttpServletRequest request) {

    String details =
        ex.getBindingResult().getAllErrors().stream()
            .map(
                error -> {
                  String fieldName = ((FieldError) error).getField();
                  String message = error.getDefaultMessage();
                  return fieldName + ": " + message;
                })
            .collect(Collectors.joining(", "));

    log.warn("Validation error: {}", details);

    ErrorResponse errorResponse =
        ErrorResponse.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .message("Validation failed")
            .details(details)
            .path(request.getRequestURI())
            .build();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  /**
   * 인증 실패 처리
   *
   * @param ex AuthenticationException
   * @param request HTTP 요청
   * @return 401 UNAUTHORIZED 응답
   */
  @ExceptionHandler({AuthenticationException.class, BadCredentialsException.class})
  public ResponseEntity<ErrorResponse> handleAuthenticationException(
      AuthenticationException ex, HttpServletRequest request) {

    log.warn("Authentication failed: {}", ex.getMessage());

    ErrorResponse errorResponse =
        ErrorResponse.builder()
            .status(HttpStatus.UNAUTHORIZED.value())
            .message("Authentication failed")
            .details(ex.getMessage())
            .path(request.getRequestURI())
            .build();

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
  }

  /**
   * 사용자 없음 처리
   *
   * @param ex UsernameNotFoundException
   * @param request HTTP 요청
   * @return 404 NOT_FOUND 응답
   */
  @ExceptionHandler(UsernameNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(
      UsernameNotFoundException ex, HttpServletRequest request) {

    log.warn("User not found: {}", ex.getMessage());

    ErrorResponse errorResponse =
        ErrorResponse.builder()
            .status(HttpStatus.NOT_FOUND.value())
            .message("User not found")
            .details(ex.getMessage())
            .path(request.getRequestURI())
            .build();

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }

  /**
   * IllegalArgumentException 처리
   *
   * @param ex IllegalArgumentException
   * @param request HTTP 요청
   * @return 400 BAD_REQUEST 응답
   */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
      IllegalArgumentException ex, HttpServletRequest request) {

    log.warn("Illegal argument: {}", ex.getMessage());

    ErrorResponse errorResponse =
        ErrorResponse.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .message("Invalid request")
            .details(ex.getMessage())
            .path(request.getRequestURI())
            .build();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  /**
   * 기타 모든 예외 처리
   *
   * @param ex Exception
   * @param request HTTP 요청
   * @return 500 INTERNAL_SERVER_ERROR 응답
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception ex, HttpServletRequest request) {

    log.error("Unhandled exception: {}", ex.getMessage(), ex);

    ErrorResponse errorResponse =
        ErrorResponse.builder()
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .message("Internal server error")
            .details(ex.getMessage())
            .path(request.getRequestURI())
            .build();

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
  }
}

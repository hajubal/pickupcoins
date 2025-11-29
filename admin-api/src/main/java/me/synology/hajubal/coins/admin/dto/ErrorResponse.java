package me.synology.hajubal.coins.admin.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

/**
 * 에러 응답 DTO
 *
 * <p>API 에러 발생 시 클라이언트에 반환하는 공통 응답 형식입니다.
 */
@Getter
@Builder
public class ErrorResponse {

  /** HTTP 상태 코드 */
  private int status;

  /** 에러 메시지 */
  private String message;

  /** 에러 상세 설명 (optional) */
  private String details;

  /** 에러 발생 시각 */
  @Builder.Default private LocalDateTime timestamp = LocalDateTime.now();

  /** 요청 경로 */
  private String path;
}

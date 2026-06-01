package com.frentis.aibackend.error;

import java.time.Instant;

/**
 * 표준 에러 응답 포맷.
 *
 * 모든 컨트롤러가 동일 포맷을 사용하도록 GlobalExceptionHandler에서 강제합니다.
 */
public record ErrorResponse(String code, String message, Instant timestamp) {

    public static ErrorResponse of(String code, String message) {
        return new ErrorResponse(code, message, Instant.now());
    }
}

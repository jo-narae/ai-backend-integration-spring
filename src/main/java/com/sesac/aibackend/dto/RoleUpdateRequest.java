package com.sesac.aibackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * 관리자의 사용자 역할 변경 요청.
 *
 * 허용 값은 "USER" 또는 "ADMIN"뿐입니다.
 * 위반 시 GlobalExceptionHandler가 VALIDATION_FAILED(400)로 응답합니다.
 */
public record RoleUpdateRequest(
        @NotBlank @Pattern(regexp = "USER|ADMIN") String role
) {}

package com.sesac.aibackend.error;

/**
 * 자원을 찾을 수 없을 때 발생.
 *
 * GlobalExceptionHandler에서 404로 매핑됩니다.
 */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }

    public static NotFoundException of(String resource, Object id) {
        return new NotFoundException(resource + " not found: " + id);
    }
}

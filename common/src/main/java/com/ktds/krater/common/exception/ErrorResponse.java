package com.ktds.krater.common.exception;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

/**
 * API 에러 응답을 위한 공통 클래스입니다.
 */
@Getter
@Builder
public class ErrorResponse {
    private final String code;
    private final String message;
    private final LocalDateTime timestamp;

    public static ErrorResponse of(String code, String message) {
        return ErrorResponse.builder()
                .code(code)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
}

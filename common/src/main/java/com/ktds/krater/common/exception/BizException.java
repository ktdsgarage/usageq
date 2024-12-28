package com.ktds.krater.common.exception;

import lombok.Getter;

/**
 * 비즈니스 로직 처리 중 발생하는 예외를 처리하는 클래스입니다.
 */
@Getter
public class BizException extends RuntimeException {
    private final String code;
    private final String message;

    public BizException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BizException(String message) {
        this("BUSINESS_ERROR", message);
    }
}

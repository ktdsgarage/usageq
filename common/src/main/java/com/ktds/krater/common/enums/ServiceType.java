package com.ktds.krater.common.enums;

/**
 * 서비스 유형을 정의하는 열거형 클래스입니다.
 */
public enum ServiceType {
    VOICE("음성"),
    VIDEO("영상"),
    MESSAGE("메시지"),
    DATA("데이터");

    private final String description;

    ServiceType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

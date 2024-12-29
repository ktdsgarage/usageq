// common/src/main/java/com/ktds/krater/common/constants/CacheConstants.java
package com.ktds.krater.common.constants;

/**
 * 캐시 관련 상수를 정의하는 클래스입니다.
 */
public final class CacheConstants {
    /**
     * 사용량 캐시의 prefix입니다.
     * Redis에서 사용량 데이터를 구분하기 위한 네임스페이스로 사용됩니다.
     */
    public static final String USAGE_CACHE_PREFIX = "usage::";

    /**
     * 사용량 캐시의 이름입니다.
     * @Cacheable 어노테이션에서 사용됩니다.
     */
    public static final String USAGE_CACHE_NAME = "usage";

    private CacheConstants() {
        throw new AssertionError("상수 클래스는 인스턴스화할 수 없습니다.");
    }
}
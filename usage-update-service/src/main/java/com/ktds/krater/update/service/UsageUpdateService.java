package com.ktds.krater.update.service;

import com.ktds.krater.common.constants.CacheConstants;
import com.ktds.krater.common.dto.UsageUpdateRequest;
import com.ktds.krater.common.entity.*;
import com.ktds.krater.update.repository.UsageRepository;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsageUpdateService {

    private final UsageRepository usageRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final MeterRegistry meterRegistry;

    @Transactional
    public void updateUsage(UsageUpdateRequest request) {
        Timer.Sample sample = Timer.start(meterRegistry);

        try {
            // 1. DB 업데이트
            Usage usage = usageRepository.findByUserIdWithLock(request.getUserId())
                    .orElseGet(() -> createNewUsage(request.getUserId()));

            usage.updateUsage(request.getType(), request.getAmount());
            usageRepository.save(usage);

            // 2. 캐시 무효화
            invalidateCache(request.getUserId());

            // 성공 메트릭 증가
            meterRegistry.counter("usage.updates.success").increment();

            // 처리 시간 기록
            sample.stop(meterRegistry.timer("usage.update.time"));

            log.info("Successfully updated usage - userId: {}", request.getUserId());
            
        } catch (Exception e) {
            // 실패 메트릭 증가
            meterRegistry.counter("usage.updates.error").increment();

            log.error("Failed to update usage - userId: {}", request.getUserId(), e);
            throw e;
        }
    }

    private Usage createNewUsage(String userId) {
        return Usage.builder()
                .userId(userId)
                .voiceUsage(VoiceUsage.builder().freeUsage(18000L).build())
                .videoUsage(VideoUsage.builder().freeUsage(7200L).build())
                .messageUsage(MessageUsage.builder().freeUsage(300L).build())
                .dataUsage(DataUsage.builder().freeUsage(5368709120L).build())
                .build();
    }

    private void invalidateCache(String userId) {
        try {
            String cacheKey = CacheConstants.USAGE_CACHE_PREFIX + userId;
            redisTemplate.delete(cacheKey);
        } catch (Exception e) {
            log.warn("Failed to invalidate cache - userId: {}", userId, e);
        }
    }
}

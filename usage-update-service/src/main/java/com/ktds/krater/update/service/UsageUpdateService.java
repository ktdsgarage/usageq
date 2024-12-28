package com.ktds.krater.update.service;

import com.ktds.krater.common.dto.UsageUpdateRequest;
import com.ktds.krater.common.entity.*;
import com.ktds.krater.update.repository.UsageRepository;
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

    @Transactional
    public void updateUsage(UsageUpdateRequest request) {
        try {
            // 1. DB 업데이트
            Usage usage = usageRepository.findByUserIdWithLock(request.getUserId())
                    .orElseGet(() -> createNewUsage(request.getUserId()));

            usage.updateUsage(request.getType(), request.getAmount());
            usageRepository.save(usage);

            // 2. 캐시 무효화
            invalidateCache(request.getUserId());
            
            log.info("Successfully updated usage - userId: {}", request.getUserId());
            
        } catch (Exception e) {
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
            redisTemplate.delete("usage:" + userId);
        } catch (Exception e) {
            log.warn("Failed to invalidate cache - userId: {}", userId, e);
        }
    }
}

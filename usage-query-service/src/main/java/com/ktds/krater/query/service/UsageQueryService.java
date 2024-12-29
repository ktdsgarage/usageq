package com.ktds.krater.query.service;

import com.ktds.krater.common.constants.CacheConstants;
import com.ktds.krater.common.dto.UsageDTO;
import com.ktds.krater.common.entity.Usage;
import com.ktds.krater.query.mapper.UsageMapper;
import com.ktds.krater.query.repository.UsageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsageQueryService {
    private final UsageRepository usageRepository;
    private final UsageMapper usageMapper;
    private final RedisTemplate<String, UsageDTO> redisTemplate;

    @Transactional(readOnly = true)
    public UsageDTO getUserUsage(String userId) {
        String cacheKey = CacheConstants.USAGE_CACHE_PREFIX + userId;

        // 1. 캐시 조회 시도
        try {
            UsageDTO cachedUsage = redisTemplate.opsForValue().get(cacheKey);
            if (cachedUsage != null) {
                log.debug("Cache hit for userId: {}", userId);
                return cachedUsage;
            }
        } catch (Exception e) {
            // 캐시 조회 실패 시에도 서비스는 계속 동작하도록 함
            log.warn("Failed to get from cache for userId: {}. Error: {}", userId, e.getMessage());
        }

        // 2. DB 조회
        Usage usage;
        try {
            usage = usageRepository.findByUserId(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        } catch (Exception e) {
            log.error("Failed to get usage from DB for userId: {}", userId, e);
            throw e;
        }

        // 3. DTO 변환
        UsageDTO usageDTO = usageMapper.toDTO(usage);

        // 4. 캐시 저장 시도
        try {
            redisTemplate.opsForValue()
                    .set(cacheKey, usageDTO, Duration.ofMinutes(30));
            log.debug("Successfully cached usage data for userId: {}", userId);
        } catch (Exception e) {
            // 캐시 저장 실패 시에도 서비스는 계속 동작하도록 함
            log.warn("Failed to cache usage data for userId: {}. Error: {}", userId, e.getMessage());
        }

        return usageDTO;
    }
}

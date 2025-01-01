// com.ktds.krater.query.service.UsageQueryService
package com.ktds.krater.query.service;

import com.ktds.krater.common.constants.CacheConstants;
import com.ktds.krater.common.dto.UsageDTO;
import com.ktds.krater.common.entity.Usage;
import com.ktds.krater.query.mapper.UsageMapper;
import com.ktds.krater.query.repository.UsageRepository;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsageQueryService {
    private final UsageRepository usageRepository;
    private final UsageMapper usageMapper;
    private final RedisTemplate<String, UsageDTO> redisTemplate;
    private final MeterRegistry meterRegistry;

    @Transactional(readOnly = true)
    public UsageDTO getUserUsage(String userId) {
        Timer.Sample sample = Timer.start(meterRegistry);

        try {
            String cacheKey = CacheConstants.USAGE_CACHE_PREFIX + userId;

            // Try cache
            UsageDTO cachedUsage = redisTemplate.opsForValue().get(cacheKey);
            if (cachedUsage != null) {
                meterRegistry.counter("usage.cache.hits").increment();
                return cachedUsage;
            }

            meterRegistry.counter("usage.cache.misses").increment();

            // Get from DB
            Usage usage = usageRepository.findByUserId(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
            UsageDTO usageDTO = usageMapper.toDTO(usage);

            // Update cache
            redisTemplate.opsForValue().set(cacheKey, usageDTO, Duration.ofMinutes(30));

            sample.stop(meterRegistry.timer("usage.response.time"));
            meterRegistry.counter("usage.requests").increment();

            return usageDTO;

        } catch (Exception e) {
            meterRegistry.counter("usage.errors").increment();
            log.error("Error getting usage for userId: {}", userId, e);
            throw e;
        }
    }
}
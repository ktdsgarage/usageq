package com.ktds.krater.query.service;

import com.ktds.krater.common.dto.UsageDTO;
import com.ktds.krater.common.entity.Usage;
import com.ktds.krater.query.mapper.UsageMapper;
import com.ktds.krater.query.repository.UsageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsageQueryService {

   private final UsageRepository usageRepository;
   private final UsageMapper usageMapper;

   @Transactional(readOnly = true)
   @Cacheable(value = "usage", key = "#userId")
   public UsageDTO getUserUsage(String userId) {
       log.info("Fetching usage data for userId: {}", userId);
       
       Usage usage = usageRepository.findByUserId(userId)
               .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
               
       return usageMapper.toDTO(usage);
   }
}

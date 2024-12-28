package com.ktds.krater.query.mapper;

import com.ktds.krater.common.dto.UsageDTO;
import com.ktds.krater.common.dto.UsageDetail;
import com.ktds.krater.common.entity.Usage;
import org.springframework.stereotype.Component;

@Component
public class UsageMapper {
   
   public UsageDTO toDTO(Usage usage) {
       return UsageDTO.builder()
               .userId(usage.getUserId())
               .voiceUsage(createDetail(usage.getVoiceUsage().getTotalUsage(),
                       usage.getVoiceUsage().getFreeUsage(),
                       usage.getVoiceUsage().getExcessUsage(),
                       "초"))
               .videoUsage(createDetail(usage.getVideoUsage().getTotalUsage(),
                       usage.getVideoUsage().getFreeUsage(),
                       usage.getVideoUsage().getExcessUsage(),
                       "초"))
               .messageUsage(createDetail(usage.getMessageUsage().getTotalUsage(),
                       usage.getMessageUsage().getFreeUsage(),
                       usage.getMessageUsage().getExcessUsage(),
                       "건"))
               .dataUsage(createDetail(usage.getDataUsage().getTotalUsage(),
                       usage.getDataUsage().getFreeUsage(),
                       usage.getDataUsage().getExcessUsage(),
                       "패킷"))
               .build();
   }

   private UsageDetail createDetail(long total, long free, long excess, String unit) {
       return UsageDetail.builder()
               .totalUsage(total)
               .freeUsage(free)
               .excessUsage(excess)
               .unit(unit)
               .build();
   }
}

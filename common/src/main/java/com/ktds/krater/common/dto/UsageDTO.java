package com.ktds.krater.common.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsageDTO {
    private String userId;
    private UsageDetail voiceUsage;
    private UsageDetail videoUsage;
    private UsageDetail messageUsage;
    private UsageDetail dataUsage;
}

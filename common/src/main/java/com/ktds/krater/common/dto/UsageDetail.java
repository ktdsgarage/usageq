package com.ktds.krater.common.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsageDetail {
    private long totalUsage;
    private long freeUsage;
    private long excessUsage;
    private String unit;
}

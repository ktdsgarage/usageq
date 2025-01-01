package com.ktds.krater.common.metrics;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetricDto {
    private String name;
    private double value;
    private long timestamp;
}

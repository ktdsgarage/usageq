// com.ktds.krater.query.controller.MetricsController
package com.ktds.krater.query.controller;

import com.ktds.krater.common.dto.ApiResponse;
import com.ktds.krater.common.metrics.MetricDto;
import io.micrometer.core.instrument.Measurement;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/metrics")
@RequiredArgsConstructor
public class MetricsController {
    private final MeterRegistry meterRegistry;

    @GetMapping
    public ApiResponse<List<MetricDto>> getMetrics() {
        List<MetricDto> metrics = new ArrayList<>();

        for (Meter meter : meterRegistry.getMeters()) {
            for (Measurement measurement : meter.measure()) {
                metrics.add(MetricDto.builder()
                        .name(meter.getId().getName())
                        .value(measurement.getValue())
                        .timestamp(System.currentTimeMillis())
                        .build());
            }
        }

        return ApiResponse.success(metrics);
    }
}
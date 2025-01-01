package com.ktds.krater.management.controller;

import com.ktds.krater.common.dto.ApiResponse;
import com.ktds.krater.common.dto.UsageUpdateRequest;
import com.ktds.krater.management.service.UsageManagementService;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "사용량 관리 API", description = "사용량 업데이트 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/usage")
public class UsageController {
    private final UsageManagementService usageManagementService;
    private final MeterRegistry meterRegistry;

    @PostMapping("/update")
    public ResponseEntity<ApiResponse<Void>> updateUsage(@RequestBody UsageUpdateRequest request) {
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            usageManagementService.updateUsage(request);
            sample.stop(meterRegistry.timer("usage.update.request.time"));
            meterRegistry.counter("usage.update.requests").increment();
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (Exception e) {
            meterRegistry.counter("usage.update.errors").increment();
            throw e;
        }
    }
}
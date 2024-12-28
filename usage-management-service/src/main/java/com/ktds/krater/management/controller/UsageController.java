package com.ktds.krater.management.controller;

import com.ktds.krater.common.dto.ApiResponse;
import com.ktds.krater.common.dto.UsageUpdateRequest;
import com.ktds.krater.management.service.UsageManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "사용량 관리 API", description = "사용량 업데이트 관련 API")
@RestController
@RequestMapping("/api/usage")
@RequiredArgsConstructor
public class UsageController {
    private final UsageManagementService usageManagementService;

    @Operation(summary = "사용량 업데이트", description = "사용자의 사용량을 업데이트합니다.")
    @PostMapping("/update")
    public ResponseEntity<ApiResponse<Void>> updateUsage(@RequestBody UsageUpdateRequest request) {
        usageManagementService.updateUsage(request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}

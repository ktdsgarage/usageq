package com.ktds.krater.query.controller;

import com.ktds.krater.common.dto.ApiResponse;
import com.ktds.krater.common.dto.UsageDTO;
import com.ktds.krater.query.service.UsageQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "사용량 조회 API", description = "사용자의 사용량 조회 API")
@RestController
@RequestMapping("/api/usage")
@RequiredArgsConstructor
public class UsageController {

   private final UsageQueryService usageQueryService;


   @Operation(summary = "사용량 조회", description = "사용자의 음성/영상/문자/데이터 사용량을 조회합니다.")
   @GetMapping("/{userId}")
   public ResponseEntity<ApiResponse<UsageDTO>> getUserUsage(
           @Parameter(description = "사용자 ID", required = true)
           @PathVariable String userId) {
       return ResponseEntity.ok(ApiResponse.success(usageQueryService.getUserUsage(userId)));
   }
}

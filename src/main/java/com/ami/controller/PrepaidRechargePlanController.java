package com.ami.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ami.dto.requests.CreatePrepaidRechargePlanRequestDto;
import com.ami.dto.requests.UpdatePrepaidRechargePlanRequestDto;
import com.ami.dto.responses.PagedPrepaidRechargePlanResponseDto;
import com.ami.dto.responses.PrepaidRechargePlanResponseDto;
import com.ami.enums.PrepaidPlanStatus;
import com.ami.enums.SourceType;
import com.ami.service.PrepaidRechargePlanService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/billing/prepaid")
@RequiredArgsConstructor
public class PrepaidRechargePlanController {

	private final PrepaidRechargePlanService prepaidRechargePlanService;

	@PostMapping("/plans")
	public ResponseEntity<PrepaidRechargePlanResponseDto> createPlan(
			@Valid @RequestBody CreatePrepaidRechargePlanRequestDto request) {

		return ResponseEntity.ok(prepaidRechargePlanService.createPlan(request));
	}

	@PutMapping("/plans/{planId}")
	public ResponseEntity<PrepaidRechargePlanResponseDto> updatePlan(@PathVariable Long planId,
			@Valid @RequestBody UpdatePrepaidRechargePlanRequestDto request) {

		return ResponseEntity.ok(prepaidRechargePlanService.updatePlan(planId, request));
	}

	@GetMapping("/plans/{planId}")
	public ResponseEntity<PrepaidRechargePlanResponseDto> getPlanById(@PathVariable Long planId) {

		return ResponseEntity.ok(prepaidRechargePlanService.getPlanById(planId));
	}

	@GetMapping("/plans")
	public ResponseEntity<PagedPrepaidRechargePlanResponseDto> getPlans(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size, @RequestParam(required = false) String search,
			@RequestParam(required = false) SourceType sourceType,
			@RequestParam(required = false) PrepaidPlanStatus status) {

		return ResponseEntity.ok(prepaidRechargePlanService.getPlans(page, size, search, sourceType, status));
	}

	@GetMapping("/devices/{deviceId}/plans")
	public ResponseEntity<List<PrepaidRechargePlanResponseDto>> getActivePlansForPrepaidDevice(@PathVariable Long deviceId) {
		return ResponseEntity.ok(prepaidRechargePlanService.getActivePlansForPrepaidDevice(deviceId));
	}

	@PatchMapping("/plans/{planId}/status")
	public ResponseEntity<PrepaidRechargePlanResponseDto> updatePlanStatus(@PathVariable Long planId,
			@RequestParam PrepaidPlanStatus status) {

		return ResponseEntity.ok(prepaidRechargePlanService.updatePlanStatus(planId, status));
	}

	@DeleteMapping("/plans/{planId}")
	public ResponseEntity<Void> deletePlan(@PathVariable Long planId) {

		prepaidRechargePlanService.deletePlan(planId);

		return ResponseEntity.noContent().build();
	}
}
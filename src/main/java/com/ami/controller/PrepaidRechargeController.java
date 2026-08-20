package com.ami.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ami.dto.requests.CreatePrepaidRechargeOrderRequestDto;
import com.ami.dto.requests.VerifyPrepaidRechargeRequestDto;
import com.ami.dto.responses.PrepaidBalanceResponseDto;
import com.ami.dto.responses.PrepaidRechargeOrderResponseDto;
import com.ami.dto.responses.PrepaidRechargeResponseDto;
import com.ami.dto.responses.PrepaidUsageLedgerResponseDto;
import com.ami.enums.RechargeStatus;
import com.ami.service.PrepaidRechargeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/billing/prepaid/recharges")
@RequiredArgsConstructor
public class PrepaidRechargeController {

	private final PrepaidRechargeService prepaidRechargeService;

	@PostMapping("/create-order")
	public ResponseEntity<PrepaidRechargeOrderResponseDto> createRechargeOrder(
			@Valid @RequestBody CreatePrepaidRechargeOrderRequestDto request) {
		return ResponseEntity.ok(prepaidRechargeService.createRechargeOrder(request));
	}

	@PostMapping("/verify")
	public ResponseEntity<PrepaidRechargeResponseDto> verifyRechargePayment(
			@Valid @RequestBody VerifyPrepaidRechargeRequestDto request) {
		return ResponseEntity.ok(prepaidRechargeService.verifyRechargePayment(request));
	}

	@GetMapping("/devices/{deviceId}/balance")
	public ResponseEntity<PrepaidBalanceResponseDto> getPrepaidBalance(@PathVariable Long deviceId) {

		return ResponseEntity.ok(prepaidRechargeService.getPrepaidBalance(deviceId));
	}

	@GetMapping("/devices/{deviceId}/usage-history")
	public ResponseEntity<List<PrepaidUsageLedgerResponseDto>> getPrepaidUsageHistory(@PathVariable Long deviceId) {

		return ResponseEntity.ok(prepaidRechargeService.getPrepaidUsageHistory(deviceId));
	}

	@GetMapping("/devices/{deviceId}/recharges")
	public ResponseEntity<Page<PrepaidRechargeResponseDto>> getDeviceRechargeHistory(@PathVariable Long deviceId,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
			@RequestParam(required = false) String search, @RequestParam(required = false) RechargeStatus status,
			@RequestParam(required = false) LocalDateTime fromDate,
			@RequestParam(required = false) LocalDateTime toDate) {

		return ResponseEntity.ok(prepaidRechargeService.getDeviceRechargeHistory(deviceId, page, size, search, status,
				fromDate, toDate));
	}
}
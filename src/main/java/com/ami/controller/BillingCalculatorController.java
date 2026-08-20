package com.ami.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ami.dto.requests.BillCalculationRequest;
import com.ami.dto.responses.BillCalculationResponseDto;
import com.ami.service.BillingCalculatorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.ami.dto.requests.PayloadBillCalculationRequest;

@RestController
@RequestMapping("/api/billing/calculator")
@RequiredArgsConstructor
public class BillingCalculatorController {

	private final BillingCalculatorService billingCalculatorService;

	@PostMapping("/calculate")
	@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
	public ResponseEntity<BillCalculationResponseDto> calculateBill(
			@Valid @RequestBody BillCalculationRequest request) {

		return ResponseEntity.ok(billingCalculatorService.calculateBill(request));
	}

	@PostMapping("/calculate-from-payload")
	@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
	public ResponseEntity<BillCalculationResponseDto> calculateBillFromPayload(
			@Valid @RequestBody PayloadBillCalculationRequest request) {

		return ResponseEntity.ok(billingCalculatorService.calculateBillFromPayload(request));
	}

}
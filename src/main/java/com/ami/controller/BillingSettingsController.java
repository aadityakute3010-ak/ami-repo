package com.ami.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.ami.dto.requests.InvoiceSettingsRequestDto;
import com.ami.dto.requests.PenaltySettingsRequestDto;
import com.ami.dto.requests.ReminderSettingsRequestDto;
import com.ami.dto.requests.TaxSettingsRequestDto;
import com.ami.dto.responses.BillingSettingsResponseDto;
import com.ami.service.BillingSettingsService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/billing/settings")
@RequiredArgsConstructor
public class BillingSettingsController {

	private final BillingSettingsService billingSettingsService;

	@GetMapping
	@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
	public ResponseEntity<BillingSettingsResponseDto> getSettings() {

		return ResponseEntity.ok(billingSettingsService.getSettings());
	}

	@PatchMapping("/invoice")
	@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
	public ResponseEntity<BillingSettingsResponseDto> updateInvoiceSettings(
			@Valid @RequestBody InvoiceSettingsRequestDto request) {

		return ResponseEntity.ok(billingSettingsService.updateInvoiceSettings(request));
	}

	@PatchMapping("/tax")
	@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
	public ResponseEntity<BillingSettingsResponseDto> updateTaxSettings(
			@Valid @RequestBody TaxSettingsRequestDto request) {

		return ResponseEntity.ok(billingSettingsService.updateTaxSettings(request));
	}

	@PatchMapping("/penalty")
	@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
	public ResponseEntity<BillingSettingsResponseDto> updatePenaltySettings(
			@Valid @RequestBody PenaltySettingsRequestDto request) {

		return ResponseEntity.ok(billingSettingsService.updatePenaltySettings(request));
	}

	@PatchMapping("/reminder")
	@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
	public ResponseEntity<BillingSettingsResponseDto> updateReminderSettings(
			@Valid @RequestBody ReminderSettingsRequestDto request) {

		return ResponseEntity.ok(billingSettingsService.updateReminderSettings(request));
	}
}
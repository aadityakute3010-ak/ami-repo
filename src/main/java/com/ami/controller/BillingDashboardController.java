package com.ami.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ami.dto.responses.BillingDashboardResponseDto;
import com.ami.service.BillingDashboardService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/billing/dashboard")
@RequiredArgsConstructor
public class BillingDashboardController {
	private final BillingDashboardService billingDashboardService;

	@GetMapping
	@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
	public ResponseEntity<BillingDashboardResponseDto> getDashboard(@RequestParam(required = false) Integer year,
			@RequestParam(required = false) Integer month) {
		return ResponseEntity.ok(billingDashboardService.getDashboard(year, month));
	}
}
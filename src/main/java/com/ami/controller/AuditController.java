package com.ami.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.ami.dto.requests.CreateAuditLogRequestDto;
import com.ami.dto.responses.AuditDashboardResponseDto;
import com.ami.dto.responses.AuditLogResponseDto;
import com.ami.dto.responses.PagedAuditLogResponseDto;
import com.ami.service.AuditService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
public class AuditController {

	private final AuditService auditService;

	@PreAuthorize("hasRole('SUPER_ADMIN')")
	@PostMapping
	public AuditLogResponseDto createAuditLog(@RequestBody CreateAuditLogRequestDto request) {

		return auditService.createAuditLog(request);
	}

	@PreAuthorize("hasRole('SUPER_ADMIN')")
	@GetMapping
	public List<AuditLogResponseDto> getAllAuditLogs() {

		return auditService.getAllAuditLogs();
	}

	@PreAuthorize("hasRole('SUPER_ADMIN')")
	@GetMapping("/module/{module}")
	public List<AuditLogResponseDto> getLogsByModule(@PathVariable String module) {

		return auditService.getLogsByModule(module);
	}

	@PreAuthorize("hasRole('SUPER_ADMIN')")
	@GetMapping("/entity/{entityId}")
	public List<AuditLogResponseDto> getLogsByEntityId(@PathVariable Long entityId) {
		return auditService.getLogsByEntityId(entityId);
	}

	@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
	@GetMapping("/billing")
	public PagedAuditLogResponseDto getBillingAuditLogs(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		return auditService.getBillingAuditLogs(page, size);
	}

	@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
	@GetMapping("/billing/dashboard")
	public AuditDashboardResponseDto getBillingAuditDashboard() {
		return auditService.getBillingAuditDashboard();
	}

	@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
	@GetMapping("/billing/timeline")
	public List<AuditLogResponseDto> getBillingActivityTimeline(@RequestParam(defaultValue = "10") int limit) {
		return auditService.getBillingActivityTimeline(limit);
	}
}
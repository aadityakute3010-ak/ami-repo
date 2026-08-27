package com.ami.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ami.mapper.ExportFileResponseMapper;
import com.ami.service.BillingReportExportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/billing/reports/export")
@RequiredArgsConstructor
public class BillingReportExportController {

	private final BillingReportExportService billingReportExportService;

	private final ExportFileResponseMapper exportFileResponseMapper;

	@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
	@GetMapping
	public ResponseEntity<byte[]> exportBillingReport(@RequestParam(required = false) String format,
			@RequestParam(required = false) Integer year, @RequestParam(required = false) Integer month) {

		return exportFileResponseMapper
				.toAttachmentResponse(billingReportExportService.exportBillingReport(format, year, month));
	}
}
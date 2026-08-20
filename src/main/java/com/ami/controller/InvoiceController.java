package com.ami.controller;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.ami.dto.requests.GenerateInvoiceRequestDto;
import com.ami.dto.responses.InvoiceResponseDto;
import com.ami.enums.InvoiceGenerationType;
import com.ami.service.InvoiceService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.ami.dto.responses.PagedInvoiceResponseDto;
import com.ami.enums.BillingType;
import com.ami.enums.InvoiceStatus;
import com.ami.enums.PaymentStatus;
import com.ami.enums.SourceType;

@RestController
@RequestMapping("/api/billing/invoices")
@RequiredArgsConstructor
public class InvoiceController {

	private final InvoiceService invoiceService;

	@PostMapping("/generate")
	@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
	public ResponseEntity<InvoiceResponseDto> generateInvoice(@Valid @RequestBody GenerateInvoiceRequestDto request) {

		return ResponseEntity.ok(invoiceService.generateInvoice(request, InvoiceGenerationType.MANUAL));
	}

	@GetMapping("/{invoiceId}")
	@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
	public ResponseEntity<InvoiceResponseDto> getInvoiceById(@PathVariable Long invoiceId) {

		return ResponseEntity.ok(invoiceService.getInvoiceById(invoiceId));
	}

	@GetMapping("/{invoiceId}/preview")
	@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
	public ResponseEntity<ByteArrayResource> previewInvoicePdf(@PathVariable Long invoiceId) {

		return invoiceService.generateInvoicePdfResponse(invoiceId, false);
	}

	@GetMapping("/{invoiceId}/download")
	@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
	public ResponseEntity<ByteArrayResource> downloadInvoicePdf(@PathVariable Long invoiceId) {

		return invoiceService.generateInvoicePdfResponse(invoiceId, true);
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
	public ResponseEntity<PagedInvoiceResponseDto> getInvoices(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size, @RequestParam(required = false) String search,
			@RequestParam(required = false) InvoiceStatus status,
			@RequestParam(required = false) PaymentStatus paymentStatus,
			@RequestParam(required = false) SourceType source, @RequestParam(required = false) BillingType billingType,
			@RequestParam(required = false) String fromDate, @RequestParam(required = false) String toDate) {

		return ResponseEntity.ok(invoiceService.getInvoices(page, size, search, status, paymentStatus, source,
				billingType, fromDate, toDate));
	}

	@PostMapping("/{invoiceId}/send-email")
	public ResponseEntity<InvoiceResponseDto> sendInvoiceEmail(@PathVariable Long invoiceId) {
		return ResponseEntity.ok(invoiceService.sendInvoiceEmail(invoiceId));
	}

}
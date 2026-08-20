package com.ami.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ami.dto.requests.CreatePaymentOrderRequestDto;
import com.ami.dto.requests.CreatePaymentRequestDto;
import com.ami.dto.requests.VerifyPaymentRequestDto;
import com.ami.dto.responses.PagedPaymentResponseDto;
import com.ami.dto.responses.PaymentOrderResponseDto;
import com.ami.dto.responses.PaymentResponseDto;
import com.ami.enums.PaymentTransactionStatus;
import com.ami.service.PaymentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.core.io.ByteArrayResource;
import com.ami.dto.responses.PaymentReceiptResponseDto;

@RestController
@RequestMapping("/api/billing/payments")
@RequiredArgsConstructor
public class PaymentController {

	private final PaymentService paymentService;

	@PostMapping
	public ResponseEntity<PaymentResponseDto> createPayment(@Valid @RequestBody CreatePaymentRequestDto request) {

		return ResponseEntity.ok(paymentService.createPayment(request));
	}

	@PostMapping("/create-order")
	public ResponseEntity<PaymentOrderResponseDto> createPaymentOrder(
			@Valid @RequestBody CreatePaymentOrderRequestDto request) {

		return ResponseEntity.ok(paymentService.createPaymentOrder(request));
	}

	@PostMapping("/verify")
	public ResponseEntity<PaymentResponseDto> verifyPayment(@Valid @RequestBody VerifyPaymentRequestDto request) {

		return ResponseEntity.ok(paymentService.verifyPayment(request));
	}

	@GetMapping("/{paymentId}")
	public ResponseEntity<PaymentResponseDto> getPaymentById(@PathVariable Long paymentId) {

		return ResponseEntity.ok(paymentService.getPaymentById(paymentId));
	}

	@GetMapping("/invoice/{invoiceId}")
	public ResponseEntity<List<PaymentResponseDto>> getPaymentsByInvoice(@PathVariable Long invoiceId) {

		return ResponseEntity.ok(paymentService.getPaymentsByInvoice(invoiceId));
	}

	@GetMapping
	public ResponseEntity<PagedPaymentResponseDto> getPayments(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size, @RequestParam(required = false) String search,
			@RequestParam(required = false) PaymentTransactionStatus status,
			@RequestParam(required = false) String fromDate, @RequestParam(required = false) String toDate) {

		return ResponseEntity.ok(paymentService.getPayments(page, size, search, status, fromDate, toDate));
	}

	@GetMapping("/{paymentId}/receipt")
	public ResponseEntity<PaymentReceiptResponseDto> getPaymentReceiptDetails(@PathVariable Long paymentId) {

		return ResponseEntity.ok(paymentService.getPaymentReceiptDetails(paymentId));
	}

	@GetMapping("/{paymentId}/receipt/preview")
	public ResponseEntity<ByteArrayResource> previewPaymentReceipt(@PathVariable Long paymentId) {

		return paymentService.generatePaymentReceiptPdfResponse(paymentId, false);
	}

	@GetMapping("/{paymentId}/receipt/download")
	public ResponseEntity<ByteArrayResource> downloadPaymentReceipt(@PathVariable Long paymentId) {

		return paymentService.generatePaymentReceiptPdfResponse(paymentId, true);
	}

}
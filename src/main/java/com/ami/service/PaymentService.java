package com.ami.service;

import java.util.List;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;

import com.ami.dto.requests.CreatePaymentOrderRequestDto;
import com.ami.dto.requests.CreatePaymentRequestDto;
import com.ami.dto.requests.VerifyPaymentRequestDto;
import com.ami.dto.responses.PagedPaymentResponseDto;
import com.ami.dto.responses.PaymentOrderResponseDto;
import com.ami.dto.responses.PaymentReceiptResponseDto;
import com.ami.dto.responses.PaymentResponseDto;
import com.ami.enums.PaymentTransactionStatus;

public interface PaymentService {

	PaymentResponseDto createPayment(CreatePaymentRequestDto request);

	PaymentOrderResponseDto createPaymentOrder(CreatePaymentOrderRequestDto request);

	PaymentResponseDto verifyPayment(VerifyPaymentRequestDto request);

	PaymentResponseDto getPaymentById(Long paymentId);

	List<PaymentResponseDto> getPaymentsByInvoice(Long invoiceId);

	PagedPaymentResponseDto getPayments(int page, int size, String search, PaymentTransactionStatus status,
			String fromDate, String toDate);
	
	PaymentReceiptResponseDto getPaymentReceiptDetails(Long paymentId);

	ResponseEntity<ByteArrayResource> generatePaymentReceiptPdfResponse(Long paymentId, boolean download);  
}
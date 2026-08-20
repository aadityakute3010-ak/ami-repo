package com.ami.service;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;

import com.ami.dto.requests.GenerateInvoiceRequestDto;
import com.ami.dto.responses.InvoiceResponseDto;
import com.ami.enums.InvoiceGenerationType;
import com.ami.dto.responses.PagedInvoiceResponseDto;
import com.ami.enums.BillingType;
import com.ami.enums.InvoiceStatus;
import com.ami.enums.PaymentStatus;
import com.ami.enums.SourceType;

public interface InvoiceService {

	InvoiceResponseDto generateInvoice(GenerateInvoiceRequestDto request, InvoiceGenerationType generationType);

	InvoiceResponseDto getInvoiceById(Long invoiceId);

	ResponseEntity<ByteArrayResource> generateInvoicePdfResponse(Long invoiceId, boolean download);

	PagedInvoiceResponseDto getInvoices(int page, int size, String search, InvoiceStatus status,
			PaymentStatus paymentStatus, SourceType source, BillingType billingType, String fromDate, String toDate);
	
	InvoiceResponseDto sendInvoiceEmail(Long invoiceId);
	
}
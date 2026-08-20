package com.ami.dto.responses;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.ami.enums.PaymentGateway;
import com.ami.enums.PaymentMethod;
import com.ami.enums.PaymentTransactionStatus;
import com.ami.enums.SourceType;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PaymentReceiptResponseDto {

	private Long paymentId;

	private String receiptNumber;

	private String transactionId;

	private LocalDateTime paymentDate;

	private BigDecimal amountPaid;

	private PaymentMethod paymentMethod;

	private PaymentGateway paymentGateway;

	private PaymentTransactionStatus paymentStatus;

	private String referenceNumber;

	private String remarks;

	private Long invoiceId;

	private String invoiceNumber;

	private BigDecimal invoiceNetAmount;

	private BigDecimal invoicePaidAmount;

	private BigDecimal invoiceBalanceAmount;

	private String invoicePaymentStatus;

	private String customerName;

	private String customerEmail;

	private String customerPhone;

	private SourceType source;

	private String meterNumber;

	private String billingPeriod;

	private LocalDate dueDate;

	private String receiptPreviewUrl;

	private String receiptDownloadUrl;
} 
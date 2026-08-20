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
public class PaymentResponseDto {

	private Long id;

	private String transactionId;

	private Long invoiceId;

	private String invoiceNumber;

	private Long customerId;

	private String customerName;

	private BigDecimal amount;

	private PaymentMethod method;

	private PaymentTransactionStatus status;

	private String referenceNumber;

	private String remarks;

	private LocalDateTime paymentDate;

	private LocalDateTime createdAt;

	private PaymentGateway gateway;

	private String razorpayOrderId;

	private String razorpayPaymentId;

	private String razorpaySignature;

	private SourceType source;

	private String billingPeriod;

	private LocalDate dueDate;

	private String meterNumber;

	private BigDecimal invoicePaidAmount;

	private BigDecimal invoiceBalanceAmount;

	private String invoicePaymentStatus;
}
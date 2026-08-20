package com.ami.dto.responses;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.ami.enums.BillingType;
import com.ami.enums.InvoiceGenerationType;
import com.ami.enums.InvoiceStatus;
import com.ami.enums.PaymentStatus;
import com.ami.enums.SourceType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InvoiceResponseDto {

	private Long id;

	private String invoiceNumber;

	private Long customerId;

	private String customerName;

	private String email;

	private String phone;

	private String meterNumber;

	private SourceType source;

	private BillingType billingType;

	private Long tariffId;

	private String tariffName;

	private BigDecimal previousReading;

	private BigDecimal currentReading;

	private BigDecimal consumption;

	private BigDecimal amount;

	private BigDecimal fixedCharge;

	private BigDecimal tax;

	private BigDecimal discount;

	private BigDecimal netAmount;
	
	private BigDecimal previousDues;

	private InvoiceStatus status;

	private PaymentStatus paymentStatus;

	private LocalDate invoiceDate;

	private LocalDate dueDate;
	
	private BigDecimal penaltyAmount;

	private Boolean penaltyApplied;

	private LocalDate billingPeriodFrom;

	private LocalDate billingPeriodTo;

	private String remarks;

	private BigDecimal paidAmount;

	private BigDecimal balanceAmount;

	private InvoiceGenerationType generationType;

	private String failureReason;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;
	
	private String pdfPreviewUrl;

	private String pdfDownloadUrl;
	
	private Integer invoiceDueDaysSnapshot;

	private Integer gracePeriodDaysSnapshot;

	private Boolean penaltyEnabledSnapshot;

	private BigDecimal penaltyPercentageSnapshot;
}
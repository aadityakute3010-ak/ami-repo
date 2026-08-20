package com.ami.dto.responses;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.ami.enums.PaymentGateway;
import com.ami.enums.PaymentMethod;
import com.ami.enums.RechargeStatus;
import com.ami.enums.SourceType;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PrepaidRechargeResponseDto {

	private Long id;

	private String rechargeNumber;

	private String transactionId;

	private Long deviceId;

	private String deviceIdentifier;

	private Long userId;

	private String customerName;

	private String customerEmail;

	private BigDecimal amount;

	private BigDecimal taxAmount;

	private BigDecimal netRechargeAmount;

	private BigDecimal creditedUnits;

	private SourceType sourceType;

	private Long tariffId;

	private String tariffName;

	private PaymentMethod paymentMethod;

	private PaymentGateway paymentGateway;

	private RechargeStatus status;

	private String referenceNumber;

	private String remarks;

	private LocalDateTime rechargeDate;

	private BigDecimal totalCreditedUnits;

	private BigDecimal totalUsedUnits;

	private BigDecimal availableUnits;

	private String receiptPreviewUrl;

	private String receiptDownloadUrl;
}
package com.ami.dto.responses;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PayloadBillingReadingResponseDto {

	private Long deviceId;

	private String deviceCode;

	private String deviceName;

	private LocalDate billingPeriodFrom;

	private LocalDate billingPeriodTo;

	private BigDecimal previousReading;

	private BigDecimal currentReading;

	private BigDecimal totalConsumption;

	private Long totalReadingDays;
}
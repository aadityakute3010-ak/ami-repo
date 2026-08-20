package com.ami.dto.responses;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.ami.enums.PrepaidBalanceStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PrepaidBalanceResponseDto {

	private Long deviceId;

	private String deviceIdentifier;

	private Long userId;

	private String customerName;

	private BigDecimal totalRechargedAmount;

	private BigDecimal totalCreditedUnits;

	private BigDecimal totalUsedUnits;

	private BigDecimal availableUnits;

	private BigDecimal lastMeterReading;

	private PrepaidBalanceStatus status;

	private LocalDateTime lastRechargeAt;

	private LocalDateTime lastConsumptionAt;
}
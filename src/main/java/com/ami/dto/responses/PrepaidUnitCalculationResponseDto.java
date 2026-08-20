package com.ami.dto.responses;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PrepaidUnitCalculationResponseDto {

	private Long deviceId;

	private String deviceIdentifier;

	private Long planId;

	private BigDecimal rechargeAmount;

	private Long tariffId;

	private String tariffName;

	private BigDecimal fixedCharge;

	private BigDecimal taxPercentage;

	private BigDecimal taxAmount;

	private BigDecimal unitPurchaseAmount;

	private BigDecimal creditedUnits;
}
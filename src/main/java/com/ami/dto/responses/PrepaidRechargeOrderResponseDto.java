package com.ami.dto.responses;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PrepaidRechargeOrderResponseDto {

	private Long rechargeId;

	private String rechargeNumber;

	private String orderId;

	private Long deviceId;

	private String deviceIdentifier;

	private Long planId;

	private BigDecimal amount;

	private BigDecimal creditedUnits;

	private Long tariffId;

	private String tariffName;

	private BigDecimal fixedCharge;

	private BigDecimal taxAmount;

	private BigDecimal unitPurchaseAmount;

	private String currency;

	private String gateway;

	private String status;

	private String keyId;
}
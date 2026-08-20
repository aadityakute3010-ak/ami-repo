package com.ami.dto.responses;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PaymentOrderResponseDto {

	private String orderId;

	private Long invoiceId;

	private String invoiceNumber;

	private BigDecimal amount;

	private String currency;

	private String gateway;

	private String status;

	private String keyId;
}
package com.ami.dto.requests;

import com.ami.enums.PaymentGateway;
import com.ami.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePrepaidRechargeRequestDto {

	@NotNull(message = "Device id is required")
	private Long deviceId;

	@NotNull(message = "Recharge plan id is required")
	private Long planId;

	@NotNull(message = "Payment method is required")
	private PaymentMethod paymentMethod;

	private PaymentGateway paymentGateway;

	private String referenceNumber;

	private String remarks;
}
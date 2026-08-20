package com.ami.dto.requests;


import com.ami.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePrepaidRechargeOrderRequestDto {

	@NotNull(message = "Device id is required")
	private Long deviceId;

	@NotNull(message = "Recharge plan id is required")
	private Long planId;
	
	@NotNull(message = "Payment method is required")
	private PaymentMethod paymentMethod;
}
package com.ami.dto.requests;

import java.math.BigDecimal;

import com.ami.enums.PaymentMethod;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyPaymentRequestDto {

	@NotNull(message = "Invoice id is required")
	private Long invoiceId;

	@NotBlank(message = "Order id is required")
	private String razorpayOrderId;

	@NotBlank(message = "Payment id is required")
	private String razorpayPaymentId;

	@NotBlank(message = "Payment signature is required")
	private String razorpaySignature;

	@NotNull(message = "Amount is required")
	@DecimalMin(value = "0.01", message = "Amount must be greater than zero")
	private BigDecimal amount;

	@NotNull(message = "Payment method is required")
	private PaymentMethod method;

	private String remarks;
}
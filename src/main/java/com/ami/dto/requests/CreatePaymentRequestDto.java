package com.ami.dto.requests;

import java.math.BigDecimal;

import com.ami.enums.PaymentGateway;
import com.ami.enums.PaymentMethod;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePaymentRequestDto {

	@NotNull(message = "Invoice id is required")
	private Long invoiceId;

	@NotNull(message = "Payment amount is required")
	@DecimalMin(value = "0.01", message = "Payment amount must be greater than zero")
	private BigDecimal amount;

	@NotNull(message = "Payment method is required")
	private PaymentMethod method;

	private PaymentGateway gateway;

	private String referenceNumber;

	private String remarks;
}
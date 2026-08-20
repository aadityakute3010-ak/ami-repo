package com.ami.dto.requests;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BillCalculationRequest {

	@NotNull(message = "Tariff id is required")
	private Long tariffId;

	@NotNull(message = "Previous reading is required")
	@DecimalMin(value = "0.0", message = "Previous reading cannot be negative")
	private BigDecimal previousReading;

	@NotNull(message = "Current reading is required")
	@DecimalMin(value = "0.0", message = "Current reading cannot be negative")
	private BigDecimal currentReading;

	@DecimalMin(value = "0.0", message = "Previous dues cannot be negative")
	private BigDecimal previousDues;
}
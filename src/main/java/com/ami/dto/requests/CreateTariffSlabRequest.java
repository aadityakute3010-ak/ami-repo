package com.ami.dto.requests;

import java.math.BigDecimal;

import com.ami.enums.TariffStatus;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTariffSlabRequest {

	@NotNull(message = "From range is required")
	@DecimalMin(value = "0.0", inclusive = true, message = "From range cannot be negative")
	@Digits(integer = 15, fraction = 4)
	private BigDecimal from;

	@DecimalMin(value = "0.0", inclusive = true, message = "To range cannot be negative")
	@Digits(integer = 15, fraction = 4)
	private BigDecimal to;

	@NotNull(message = "Rate is required")
	@DecimalMin(value = "0.0", inclusive = true, message = "Rate cannot be negative")
	@Digits(integer = 15, fraction = 4)
	private BigDecimal rate;

	@NotNull(message = "Fixed charge is required")
	@DecimalMin(value = "0.0", inclusive = true, message = "Fixed charge cannot be negative")
	@Digits(integer = 17, fraction = 2)
	private BigDecimal fixedCharge;

	@NotNull(message = "Tax is required")
	@DecimalMin(value = "0.0", inclusive = true, message = "Tax cannot be negative")
	@Digits(integer = 3, fraction = 4)
	private BigDecimal tax;

	@NotNull(message = "Status is required")
	private TariffStatus status;

	@Size(max = 1000, message = "Description cannot exceed 1000 characters")
	private String description;
}
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
public class UpdateTariffSlabRequest {

	@NotNull(message = "From range is required")
	@DecimalMin(value = "0.0", inclusive = true)
	@Digits(integer = 15, fraction = 4)
	private BigDecimal from;

	@DecimalMin(value = "0.0", inclusive = true)
	@Digits(integer = 15, fraction = 4)
	private BigDecimal to;

	@NotNull(message = "Rate is required")
	@DecimalMin(value = "0.0", inclusive = true)
	@Digits(integer = 15, fraction = 4)
	private BigDecimal rate;

	@NotNull(message = "Fixed charge is required")
	@DecimalMin(value = "0.0", inclusive = true)
	@Digits(integer = 17, fraction = 2)
	private BigDecimal fixedCharge;

	@NotNull(message = "Tax is required")
	@DecimalMin(value = "0.0", inclusive = true)
	@Digits(integer = 3, fraction = 4)
	private BigDecimal tax;

	@NotNull(message = "Status is required")
	private TariffStatus status;

	@Size(max = 1000)
	private String description;
}
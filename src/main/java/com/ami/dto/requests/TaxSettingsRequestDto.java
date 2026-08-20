package com.ami.dto.requests;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaxSettingsRequestDto {

	@NotNull(message = "Tax enabled flag is required")
	private Boolean taxEnabled;

	@DecimalMin(value = "0.0", message = "Default tax percentage cannot be negative")
	private BigDecimal defaultTaxPercentage;
}
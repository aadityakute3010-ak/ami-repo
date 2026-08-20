package com.ami.dto.requests;

import java.math.BigDecimal;

import com.ami.enums.SourceType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePrepaidRechargePlanRequestDto {

	@NotBlank(message = "Plan name is required")
	private String planName;

	@NotNull(message = "Plan amount is required")
	@DecimalMin(value = "500.00", message = "Minimum prepaid recharge amount is 500")
	private BigDecimal amount;

	@NotNull(message = "Source type is required")
	private SourceType sourceType;

	private String description;
}
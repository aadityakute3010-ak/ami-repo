package com.ami.dto.requests;

import java.math.BigDecimal;

import com.ami.enums.SourceType;
import com.ami.enums.TariffCategory;
import com.ami.enums.TariffStatus;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTariffRequest {

	@NotBlank(message = "Tariff name is required")
	@Size(max = 150, message = "Tariff name cannot exceed 150 characters")
	private String name;

	@NotNull(message = "Source is required")
	private SourceType source;

	@NotNull(message = "Category is required")
	private TariffCategory category;

	@NotBlank(message = "Unit is required")
	@Size(max = 30, message = "Unit cannot exceed 30 characters")
	private String unit;

	@NotNull(message = "Rate is required")
	@DecimalMin(value = "0.0", inclusive = true, message = "Rate cannot be negative")
	@Digits(integer = 15, fraction = 4, message = "Rate can have up to 15 integer digits and 4 decimal places")
	private BigDecimal rate;

	@NotNull(message = "Fixed charge is required")
	@DecimalMin(value = "0.0", inclusive = true, message = "Fixed charge cannot be negative")
	@Digits(integer = 17, fraction = 2, message = "Fixed charge can have up to 17 integer digits and 2 decimal places")
	private BigDecimal fixedCharge;

	@NotNull(message = "Tax is required")
	@DecimalMin(value = "0.0", inclusive = true, message = "Tax cannot be negative")
	@Digits(integer = 3, fraction = 4, message = "Tax can have up to 3 integer digits and 4 decimal places")
	private BigDecimal tax;

	@NotNull(message = "Status is required")
	private TariffStatus status;

	@Size(max = 1000, message = "Description cannot exceed 1000 characters")
	private String description;
}
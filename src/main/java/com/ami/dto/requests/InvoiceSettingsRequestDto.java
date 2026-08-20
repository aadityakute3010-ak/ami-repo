package com.ami.dto.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvoiceSettingsRequestDto {

	@NotBlank(message = "Invoice prefix is required")
	private String invoicePrefix;

	@NotNull(message = "Invoice due days is required")
	@Min(value = 1, message = "Invoice due days must be at least 1")
	private Integer invoiceDueDays;
	
	@NotBlank(message = "Currency is required")
	private String currency;
}
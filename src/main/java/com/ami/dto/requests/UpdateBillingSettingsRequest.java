package com.ami.dto.requests;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateBillingSettingsRequest {

	@NotBlank(message = "Invoice prefix is required")
	@Size(max = 20, message = "Invoice prefix cannot exceed 20 characters")
	private String invoicePrefix;

	@NotNull(message = "Default due days is required")
	@Min(value = 0, message = "Default due days cannot be negative")
	private Integer defaultDueDays;

	@NotNull(message = "Late penalty percentage is required")
	@DecimalMin(
			value = "0.0",
			message = "Late penalty percentage cannot be negative")
	private BigDecimal latePenaltyPercentage;

	@NotNull(message = "Grace period days is required")
	@Min(value = 0, message = "Grace period days cannot be negative")
	private Integer gracePeriodDays;

	@NotNull(message = "Payment reminder setting is required")
	private Boolean paymentReminderEnabled;

	@NotNull(message = "Reminder days before due is required")
	@Min(value = 0, message = "Reminder days before due cannot be negative")
	private Integer reminderDaysBeforeDue;

	@NotNull(message = "Overdue reminder interval is required")
	@Min(
			value = 1,
			message = "Overdue reminder interval must be at least 1 day")
	private Integer overdueReminderIntervalDays;

	@NotBlank(message = "Currency is required")
	@Size(max = 10, message = "Currency cannot exceed 10 characters")
	private String currency;
}
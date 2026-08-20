package com.ami.dto.requests;

import java.math.BigDecimal;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BillingSettingsRequestDto {

    @NotBlank(message = "Invoice prefix is required")
    private String invoicePrefix;

    @NotNull(message = "Invoice due days is required")
    @Min(value = 1, message = "Invoice due days must be at least 1")
    private Integer invoiceDueDays;

    @NotNull(message = "Tax enabled flag is required")
    private Boolean taxEnabled;

    @DecimalMin(value = "0.0", message = "Default tax percentage cannot be negative")
    private BigDecimal defaultTaxPercentage;

    @NotNull(message = "Penalty enabled flag is required")
    private Boolean penaltyEnabled;

    @DecimalMin(value = "0.0", message = "Penalty percentage cannot be negative")
    private BigDecimal penaltyPercentage;

    @NotNull(message = "Grace period days is required")
    @Min(value = 0, message = "Grace period days cannot be negative")
    private Integer gracePeriodDays;

    @NotNull(message = "Reminder enabled flag is required")
    private Boolean reminderEnabled;

    @NotNull(message = "Reminder before due days is required")
    @Min(value = 0, message = "Reminder before due days cannot be negative")
    private Integer reminderBeforeDueDays;
}
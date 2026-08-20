package com.ami.dto.requests;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PenaltySettingsRequestDto {

    @NotNull(message = "Penalty enabled flag is required")
    private Boolean penaltyEnabled;

    @DecimalMin(value = "0.0", message = "Penalty percentage cannot be negative")
    private BigDecimal penaltyPercentage;

    @NotNull(message = "Grace period days is required")
    @Min(value = 0, message = "Grace period days cannot be negative")
    private Integer gracePeriodDays;
}
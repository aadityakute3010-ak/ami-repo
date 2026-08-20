package com.ami.dto.requests;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenerateInvoiceRequestDto {

    @NotNull(message = "Device id is required")
    private Long deviceId;

    @NotNull(message = "Tariff id is required")
    private Long tariffId;

    @NotBlank(message = "Billing period from date is required")
    private String billingPeriodFrom;

    @NotBlank(message = "Billing period to date is required")
    private String billingPeriodTo;

    private BigDecimal discount;

    private String remarks;
}
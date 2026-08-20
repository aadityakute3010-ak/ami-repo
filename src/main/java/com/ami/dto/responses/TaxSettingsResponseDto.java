package com.ami.dto.responses;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TaxSettingsResponseDto {

    private Boolean taxEnabled;

    private BigDecimal defaultTaxPercentage;
}
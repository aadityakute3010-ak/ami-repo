package com.ami.dto.responses;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PenaltySettingsResponseDto {

    private Boolean penaltyEnabled;

    private BigDecimal penaltyPercentage;

    private Integer gracePeriodDays;
}
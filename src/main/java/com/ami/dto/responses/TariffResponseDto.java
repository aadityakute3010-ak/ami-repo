package com.ami.dto.responses;

import com.ami.enums.SourceType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TariffResponseDto {

    private Long id;

    private String tariffName;

    private SourceType source;

    private Double ratePerUnit;

    private Double fixedCharge;

    private Double taxPercentage;

    private String description;

    private Boolean active;
}
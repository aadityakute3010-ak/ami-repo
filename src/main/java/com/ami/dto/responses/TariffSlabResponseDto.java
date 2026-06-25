package com.ami.dto.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TariffSlabResponseDto {

    private Long id;

    private Long tariffId;

    private Double fromUnit;

    private Double toUnit;

    private Double ratePerUnit;
}
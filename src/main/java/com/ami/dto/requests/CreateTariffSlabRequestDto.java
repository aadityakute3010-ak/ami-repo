package com.ami.dto.requests;

import lombok.Data;

@Data
public class CreateTariffSlabRequestDto {

    private Double fromUnit;

    private Double toUnit;

    private Double ratePerUnit;
}
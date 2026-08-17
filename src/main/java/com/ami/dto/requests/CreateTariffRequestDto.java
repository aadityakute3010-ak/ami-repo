package com.ami.dto.requests;

import com.ami.enums.SourceType;

import lombok.Data;

@Data
public class CreateTariffRequestDto {

    private String tariffName;

    private SourceType source;

    private Double ratePerUnit;

    private Double fixedCharge;

    private Double taxPercentage;

    private String description;
}
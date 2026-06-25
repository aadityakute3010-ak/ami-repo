package com.ami.dto.requests;

import lombok.Data;

@Data
public class UpdateTariffRequestDto {

    private Double ratePerUnit;

    private Double fixedCharge;

    private Double taxPercentage;

    private Boolean active;

    private String description;
}
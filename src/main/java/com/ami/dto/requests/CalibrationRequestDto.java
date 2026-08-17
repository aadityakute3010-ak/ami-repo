package com.ami.dto.requests;

import lombok.Data;

@Data
public class CalibrationRequestDto {

    private String calibrationValue;

    private String requestedBy;

    private String remarks;
}
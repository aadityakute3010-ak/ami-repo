package com.ami.dto.requests;

import lombok.Data;

@Data 
public class GasTelemetryRequestDto {

    private Double gasFlow;

    private Double gasPressure;

    private Double totalConsumption;

    private Double batteryLevel;

    private Double signalStrength;
}

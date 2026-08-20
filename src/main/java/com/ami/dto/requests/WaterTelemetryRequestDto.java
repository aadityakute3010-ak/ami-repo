package com.ami.dto.requests;

import lombok.Data;

@Data
public class WaterTelemetryRequestDto {

    private Double flowRate;

    private Double pressure;

    private Double totalConsumption;

    private Double batteryLevel;

    private Double signalStrength;
}
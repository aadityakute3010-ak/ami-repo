package com.ami.dto.requests;

import lombok.Data;

@Data 
public class EnergyTelemetryRequestDto {

    private Double voltage;

    private Double current;

    private Double power;

    private Double frequency;

    private Double powerFactor;

    private Double energyConsumed;

    private Double batteryLevel;

    private Double signalStrength;
}
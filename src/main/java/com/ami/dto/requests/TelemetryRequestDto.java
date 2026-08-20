package com.ami.dto.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TelemetryRequestDto {

	@NotBlank 
    private String deviceId;

    // ENERGY
    private Double voltage;
    private Double current;
    private Double power;
    private Double frequency;
    private Double powerFactor;
    private Double energyConsumed;

    // WATER
    private Double flowRate;
    private Double pressure;

    // GAS
    private Double gasFlow;
    private Double gasPressure;

    // SOLAR
    private Double solarVoltage;
    private Double solarCurrent;
    private Double solarPower;
    private Double energyGenerated;

    // COMMON
    private Double totalConsumption;
    private Double batteryLevel;
    private Double signalStrength;
}
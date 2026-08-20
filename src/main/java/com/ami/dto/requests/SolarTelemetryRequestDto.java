package com.ami.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder 
public class SolarTelemetryRequestDto {

    private Double solarVoltage;

    private Double solarCurrent;

    private Double solarPower;

    private Double energyGenerated;

    private Double batteryLevel;

    private Double signalStrength;
}
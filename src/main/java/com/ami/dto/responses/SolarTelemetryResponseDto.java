package com.ami.dto.responses;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SolarTelemetryResponseDto {

    private Double solarVoltage;
    private Double solarCurrent;
    private Double solarPower;
    private Double energyGenerated;
    private Double batteryLevel;
    private Double signalStrength;
    private LocalDateTime readingTime;
}
package com.ami.dto.responses;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EnergyTelemetryResponseDto {

    private Double voltage;
    private Double current;
    private Double power;
    private Double frequency;
    private Double powerFactor;
    private Double energyConsumed;
    private Double batteryLevel;
    private Double signalStrength;
    private LocalDateTime readingTime;
}
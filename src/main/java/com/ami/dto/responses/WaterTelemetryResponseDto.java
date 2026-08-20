package com.ami.dto.responses;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WaterTelemetryResponseDto {

    private Double flowRate;
    private Double pressure;
    private Double totalConsumption;
    private Double batteryLevel;
    private Double signalStrength;
    private LocalDateTime readingTime;
}
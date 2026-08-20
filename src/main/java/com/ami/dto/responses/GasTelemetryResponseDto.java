package com.ami.dto.responses;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GasTelemetryResponseDto {

    private Double gasFlow;
    private Double gasPressure;
	private Double gasVolume;
    private Double totalConsumption;
    private Double batteryLevel;
    private Double signalStrength;
    private LocalDateTime readingTime;
	private Double temperature;

	private String pipelineHealth;
    
}
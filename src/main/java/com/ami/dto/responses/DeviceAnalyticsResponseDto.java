package com.ami.dto.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeviceAnalyticsResponseDto {

    private Double totalConsumption;

    private Double averageConsumption;

    private Double peakConsumption;

    private Double averagePressure;

    private Double averageTemperature;

    private Double averageFlowRate;

    private Long totalReadings;
    
    private Double forecastConsumption;

    private Double leakagePercentage;
    
    private Double averageBatteryLevel;

    private Double averagePipelineHealth;

    private Double averageSensorHealth;

    private Double onlinePercentage;

    private Double offlinePercentage;
}
package com.ami.dto.responses;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WaterAnalyticsResponseDto {

    private Double totalConsumption;

    private Double averageConsumption;

    private Double maximumConsumption;

    private Double minimumConsumption;

    private Double averageFlowRate;

    private Double averagePressure;

    private Double averageBatteryLevel;

    private Double averageSignalStrength;

    private Double averageTemperature;

    private Long totalDevices;

    private Long onlineDevices;

    private Long offlineDevices;

    private Long leakDetectedDevices;

    private Long lowPressureDevices;

    private Long activeAlerts;

    private List<Double> consumptionTrend;

    private List<Double> flowTrend;

    private List<Double> pressureTrend;

    private List<String> labels;
}
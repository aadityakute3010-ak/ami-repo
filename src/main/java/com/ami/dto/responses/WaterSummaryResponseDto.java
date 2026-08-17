package com.ami.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WaterSummaryResponseDto {

    private Double todayConsumption;

    private Double yesterdayConsumption;

    private Double monthlyConsumption;

    private Double averageDailyConsumption;

    private Double peakFlowRate;

    private Double averageFlowRate;

    private Double averagePressure;

    private Double averageBatteryLevel;

    private Double averageSignalStrength;

    private Long activeDevices;

    private Long offlineDevices;

    private Long activeAlerts;

    private Long leaksDetected;
}
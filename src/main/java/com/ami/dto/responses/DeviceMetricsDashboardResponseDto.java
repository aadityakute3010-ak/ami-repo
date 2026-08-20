package com.ami.dto.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeviceMetricsDashboardResponseDto {

    private Long totalDevices;
    private Long onlineDevices;
    private Long offlineDevices;
    private Long leakDetectedDevices;

    private Double totalConsumption;
    private Double averagePressure;
    private Double averageTemperature;
    private Double averageFlowRate;
}
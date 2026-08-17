package com.ami.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WaterDashboardResponseDto {

    private Long totalDevices;

    private Long onlineDevices;

    private Long offlineDevices;

    private Long activeAlerts;

    private Long leakDetectedDevices;

    private Long lowPressureDevices;

    private Long valveOpenDevices;

    private Long valveClosedDevices;

    private Double todayConsumption;
    
    private Double monthlyConsumption;

    private Double averageConsumption;

    private Double maximumConsumption;

    private Double minimumConsumption;

    private Long lowBatteryDevices;

    private Long poorSignalDevices;

    private Long tamperDevices;

    private Long pumpRunningDevices;

    private Long pumpStoppedDevices;

    private Double averageFlowRate;

    private Double averagePressure;

    private Double averageBatteryLevel;

    private Double averageSignalStrength;

    private Long pendingOperations;
    
    private Long activeInstallations;

    private Long activeServiceEngineers;

    private Long activeMaintenance;

    private Long openIssues;

    private Long acknowledgedOperations;

    private Long resolvedOperations;
    
}
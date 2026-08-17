package com.ami.dto.responses;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeviceDashboardResponseDto {

    private Long totalDevices;

    private Long onlineDevices;

    private Long offlineDevices;

    private Long leakDetectedDevices;

    private Double totalConsumption;

    private Double averagePressure;

    private Double averageTemperature;

    private Double averageFlowRate;
    
    private Long energyDevices;

    private Long waterDevices;

    private Long gasDevices;

    private Long solarDevices;

    private Long activeOperations;

    private Long pendingOperations;

    private Long resolvedOperations;
    
    private Double averageBatteryLevel;

    private Double averageSignalStrength;

    private Double maximumConsumption;

    private Double minimumConsumption;

    private Long lowBatteryDevices;

    private Long poorSignalDevices;

    private Long tamperDevices;

    private Long valveOpenDevices;

    private Long valveClosedDevices;
    
    private Long pumpRunningDevices;

    private Long pumpStoppedDevices;
    
    private DashboardSummaryResponseDto summary;

    private DeviceHealthChartDto healthChart;

    private DeviceStatusChartDto statusChart;

    private List<OfflineDeviceDto> recentOfflineDevices;
    
 // ==========================================
 // Gas Module
 // ==========================================

 private Double averageGasConcentration;

 private Double averageGasDensity;

 private Double totalGasFlow;

 private Long alarmActiveDevices;

 private Long emergencyShutdownDevices;

 private Long gasQualityNormalDevices;

 private Long gasQualityWarningDevices;

 private Long gasQualityCriticalDevices;
}
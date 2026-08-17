package com.ami.dto.requests;

import java.time.LocalDateTime;

import com.ami.enums.PumpStatus;
import com.ami.enums.SourceType;

import lombok.Data;

@Data
public class CreateDeviceTelemetryRequestDto {

    private String deviceId;

    private SourceType sourceType;

    private Double flowRate;

    private Double pressure;

    private Double temperature;

    private Double consumption;

    private Boolean leakDetected;

    private Boolean deviceOnline;

    private String status;
    
    private Double batteryLevel;

    private String valveStatus;
    
    private PumpStatus pumpStatus;

    private Double pipelineHealthScore;

    private Double sensorHealthScore;
    
    private Double ph;

    private Double tds;

    private Double turbidity;

    private Double conductivity;

    private Double dissolvedOxygen;

    private Double chlorineLevel;
    
    private Double runtimeHours;

    private LocalDateTime lastStartedAt;

    private LocalDateTime lastStoppedAt;
    
    private Double estimatedWaterLoss;

    private String leakLocation;
    
 // ==========================================
 // Gas Module
 // ==========================================

 private Double totalFlow;

 private Double gasConcentration;

 private Double gasDensity;

 private String gasQuality;

 private String leakSeverity;

 private Boolean alarmActive;

 private Boolean emergencyShutdown;
}
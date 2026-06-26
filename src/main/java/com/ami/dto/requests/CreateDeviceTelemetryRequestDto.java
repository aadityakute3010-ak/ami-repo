package com.ami.dto.requests;

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

    private Double pipelineHealthScore;

    private Double sensorHealthScore;
}
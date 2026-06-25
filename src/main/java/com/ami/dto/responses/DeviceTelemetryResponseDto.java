package com.ami.dto.responses;

import java.time.LocalDateTime;

import com.ami.enums.SourceType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeviceTelemetryResponseDto {

    private Long id;

    private String deviceId;

    private SourceType sourceType;

    private Double flowRate;

    private Double pressure;

    private Double temperature;

    private Double consumption;

    private Boolean leakDetected;

    private Boolean deviceOnline;

    private String status;

    private LocalDateTime readingTime;
    
    private Double batteryLevel;

    private String valveStatus;

    private Double pipelineHealthScore;

    private Double sensorHealthScore;
}
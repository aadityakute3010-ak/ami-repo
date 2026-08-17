package com.ami.dto.responses;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeviceHealthResponseDto {

    private String deviceId;

    private Double batteryLevel;

    private Integer signalStrength;

    private Double temperature;

    private Double pressure;

    private String valveStatus;

    private String pumpStatus;

    private Boolean communicationStatus;

    private String firmwareVersion;

    private LocalDateTime lastCommunication;

    private Integer healthScore;

    private String overallHealth;

    private String recommendation;
}
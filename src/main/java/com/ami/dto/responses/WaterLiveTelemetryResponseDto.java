package com.ami.dto.responses;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WaterLiveTelemetryResponseDto {

    private String deviceId;

    private String deviceName;

    private String meterNumber;

    private String source;

    private Double flowRate;

    private Double pressure;

    private Double consumption;

    private Double batteryLevel;

    private Integer signalStrength;

    private Double temperature;

    private Double voltage;

    private Double current;

    private Boolean tamperDetected;

    private Boolean leakDetected;

    private String valveStatus;

    private Boolean deviceOnline;

    private Double latitude;

    private Double longitude;

    private LocalDateTime readingTime;
}
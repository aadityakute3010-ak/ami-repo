package com.ami.dto.responses;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GasLiveTelemetryResponseDto {

    private String deviceId;

    private Double pressure;

    private Double flowRate;

    private Double totalFlow;

    private Double consumption;

    private Double gasConcentration;

    private Double gasDensity;

    private String gasQuality;

    private Double temperature;

    private Boolean leakDetected;

    private String leakSeverity;

    private String valveStatus;

    private Boolean emergencyShutdown;

    private Boolean alarmActive;

    private Boolean deviceOnline;

    private Double batteryLevel;

    private Integer signalStrength;

    private LocalDateTime readingTime;

}
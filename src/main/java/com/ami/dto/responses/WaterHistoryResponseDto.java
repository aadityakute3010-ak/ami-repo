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
public class WaterHistoryResponseDto {

    private Long id;

    private String deviceId;

    private String meterNumber;

    private Double flowRate;

    private Double pressure;

    private Double consumption;

    private Double batteryLevel;

    private Integer signalStrength;

    private Double temperature;

    private Double voltage;

    private Double current;

    private Boolean leakDetected;

    private Boolean tamperDetected;

    private String valveStatus;

    private LocalDateTime readingTime;
}
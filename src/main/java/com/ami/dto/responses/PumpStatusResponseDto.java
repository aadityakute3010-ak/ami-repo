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
public class PumpStatusResponseDto {

    private String deviceId;

    private String deviceName;

    private String pumpName;

    private Boolean running;

    private String status;

    private Double pressure;

    private Double flowRate;

    private Double powerConsumption;

    private Double runtimeHours;

    private LocalDateTime lastStartedAt;

    private LocalDateTime lastStoppedAt;

    private LocalDateTime updatedAt;
}
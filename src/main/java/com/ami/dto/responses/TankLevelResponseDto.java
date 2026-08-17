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
public class TankLevelResponseDto {

    private String deviceId;

    private String deviceName;

    private String tankName;

    private Double currentLevel;

    private Double maximumCapacity;

    private Double minimumCapacity;

    private Double availableCapacity;

    private Double percentageFilled;

    private String status;

    private LocalDateTime readingTime;
    
    private Boolean overflow;

    private Boolean lowLevel;

    private Boolean criticalLevel;

    private Double inflowRate;

    private Double outflowRate;

    private Double dailyConsumption;

    private Integer healthScore;

    private String recommendation;
}
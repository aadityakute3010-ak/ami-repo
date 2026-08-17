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
public class WaterQualityResponseDto {

	private String deviceId;

	private String deviceName;

	private String meterNumber;

	private Double ph;

	private Double tds;

	private Double turbidity;

	private Double conductivity;

	private Double dissolvedOxygen;

	private Double chlorineLevel;

	private Double pressure;

	private Double temperature;

	private Double flowRate;

	private Double consumption;

	private Double pipelineHealthScore;

	private Double sensorHealthScore;

	private Boolean leakDetected;

	private Boolean tamperDetected;

	private String qualityStatus;

	private LocalDateTime readingTime;
    
    
}
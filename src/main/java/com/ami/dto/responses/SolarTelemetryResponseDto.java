package com.ami.dto.responses;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SolarTelemetryResponseDto {

	private Double solarVoltage;
	private Double solarCurrent;
	private Double solarPower;
	private Double energyGenerated;
	private Double batteryLevel;
	private Double signalStrength;
	private LocalDateTime readingTime;
	private Double solarGeneration;

	private Double solarConsumption;

	private Double panelTemperature;

	private Double irradiance;

	private String inverterStatus;

	private Double batteryStorage;

	private Double gridImport;

	private Double gridExport;

	private Double efficiency;
}
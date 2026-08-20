package com.ami.dto.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TelemetryRequestDto {

	@NotBlank
	private String deviceId;

	// ENERGY
	private Double voltage;
	private Double current;
	private Double power;
	private Double frequency;
	private Double powerFactor;
	private Double energyConsumed;
	private Double activePower;
	private Double reactivePower;
	private Double apparentPower;
	private Double load;
	private Double demand;

	// WATER
	private Double flowRate;
	private Double pressure;
	private Double tankLevel;

	private String pumpStatus;

	private Boolean leakDetected;

	// GAS
	private Double gasFlow;
	private Double gasPressure;
	private Double gasVolume;
	private Double temperature;
	private String pipelineHealth;

	// SOLAR
	private Double solarVoltage;
	private Double solarCurrent;
	private Double solarPower;
	private Double energyGenerated;
	private Double solarGeneration;

	private Double solarConsumption;

	private Double panelTemperature;

	private Double irradiance;

	private String inverterStatus;

	private Double batteryStorage;

	private Double gridImport;

	private Double gridExport;

	private Double efficiency;

	// COMMON
	private Double totalConsumption;
	private Double batteryLevel;
	private Double signalStrength;
}
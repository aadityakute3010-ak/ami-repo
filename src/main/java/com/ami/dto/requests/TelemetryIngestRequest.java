package com.ami.dto.requests;

import com.ami.enums.SensorStatus;
import com.ami.enums.ValveStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TelemetryIngestRequest {

	// Device Identity
	@NotBlank(message = "Device Id is required")
	private String deviceId;

	// Meter Readings
	@NotNull(message = "Start Reading is required")
	private Double startReading;

	@NotNull(message = "End Reading is required")
	private Double endReading;

	private Double startBalance;

	private Double endBalance;

	// Communication Data
	private Integer batteryPercentage;

	private Integer signalQuality;

	private Integer signalPower;

	private Integer snr;

	// Device Snapshot
	private String firmwareVersion;

	private String simNumber;

	private String consumerNumber;

	// Device State
	private ValveStatus valveStatus;

	private SensorStatus sensorStatus;

	// Raw Payload
	private String rawPayload;

	// ENERGY
	private Double voltage;
	private Double current;
	private Double power;
	private Double frequency;
	private Double powerFactor;
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
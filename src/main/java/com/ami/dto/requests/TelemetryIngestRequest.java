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

    //Device Snapshot
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
    private Double energyConsumed;

    // WATER
    private Double flowRate;
    private Double pressure;

    // GAS
    private Double gasFlow;
    private Double gasPressure;

    // SOLAR
    private Double solarVoltage;
    private Double solarCurrent;
    private Double solarPower;
    private Double energyGenerated;
    
}
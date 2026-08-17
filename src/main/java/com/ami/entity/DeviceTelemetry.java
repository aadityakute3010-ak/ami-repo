package com.ami.entity;

import java.time.LocalDateTime;

import com.ami.enums.PumpStatus;
import com.ami.enums.SourceType;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "device_telemetry",
        indexes = {

                @Index(
                        name = "idx_device_telemetry_device_id",
                        columnList = "deviceId"),

                @Index(
                        name = "idx_device_telemetry_source_type",
                        columnList = "sourceType"),

                @Index(
                        name = "idx_device_telemetry_reading_time",
                        columnList = "readingTime"),

                @Index(
                        name = "idx_device_telemetry_online",
                        columnList = "deviceOnline"),

                @Index(
                        name = "idx_device_telemetry_leak",
                        columnList = "leakDetected"),

                @Index(
                        name = "idx_device_telemetry_status",
                        columnList = "status"),
                
                @Index(
                	    name = "idx_device_telemetry_alarm",
                	    columnList = "alarmActive"),

                	@Index(
                	    name = "idx_device_telemetry_emergency",
                	    columnList = "emergencyShutdown")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceTelemetry extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String deviceId;

    @Enumerated(EnumType.STRING)
    private SourceType sourceType;
    
    @Enumerated(EnumType.STRING)
    private PumpStatus pumpStatus;
    
    private Double flowRate;

    private Double pressure;

    private Double temperature;
    
    private Double ph;

    private Double tds;

    private Double turbidity;

    private Double conductivity;

    private Double dissolvedOxygen;

    private Double chlorineLevel;

    private Double consumption;

    private Boolean leakDetected;

    private Boolean deviceOnline;
    
    private Double batteryLevel;
    
    private Integer signalStrength;

    private Double voltage;

    private Double current;

    private Boolean tamperDetected;

    private String valveStatus;

    private Double pipelineHealthScore;

    private Double sensorHealthScore;

    private String status;

    private LocalDateTime readingTime;
    
    private Double runtimeHours;

    private LocalDateTime lastStartedAt;

    private LocalDateTime lastStoppedAt;
    
    private Double estimatedWaterLoss;

    private String leakLocation;
    
    private Double totalFlow;

    private String leakSeverity;

    private Double gasConcentration;

    private Double gasDensity;

    private String gasQuality;

    private Double differentialPressure;

    private Boolean emergencyShutdown;

    private Boolean alarmActive;
}
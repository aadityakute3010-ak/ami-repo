package com.ami.entity.telemetry;

import java.time.LocalDateTime;

import com.ami.entity.BaseEntity;
import com.ami.enums.PumpStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "water_telemetry",
        indexes = {

                @Index(
                        name = "idx_water_device_id",
                        columnList = "deviceId"),

                @Index(
                        name = "idx_water_reading_time",
                        columnList = "readingTime"),

                @Index(
                        name = "idx_water_online",
                        columnList = "deviceOnline"),

                @Index(
                        name = "idx_water_leak",
                        columnList = "leakDetected"),

                @Index(
                        name = "idx_water_status",
                        columnList = "status"),

                @Index(
                        name = "idx_water_alarm",
                        columnList = "alarmActive"),

                @Index(
                        name = "idx_water_emergency",
                        columnList = "emergencyShutdown")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WaterTelemetry extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String deviceId;

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

    private Boolean emergencyShutdown;

    private Boolean alarmActive;
    
    private Double voltage;

    private Double current;
}
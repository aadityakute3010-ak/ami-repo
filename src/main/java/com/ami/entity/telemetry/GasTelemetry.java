package com.ami.entity.telemetry;

import java.time.LocalDateTime;

import com.ami.entity.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "gas_telemetry",
        indexes = {

                @Index(
                        name = "idx_gas_device_id",
                        columnList = "deviceId"),

                @Index(
                        name = "idx_gas_reading_time",
                        columnList = "readingTime"),

                @Index(
                        name = "idx_gas_online",
                        columnList = "deviceOnline"),

                @Index(
                        name = "idx_gas_leak",
                        columnList = "leakDetected"),

                @Index(
                        name = "idx_gas_status",
                        columnList = "status"),

                @Index(
                        name = "idx_gas_alarm",
                        columnList = "alarmActive"),

                @Index(
                        name = "idx_gas_emergency",
                        columnList = "emergencyShutdown")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GasTelemetry extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String deviceId;

    private Double pressure;

    private Double temperature;

    private Double flowRate;

    private Double consumption;

    private Boolean leakDetected;

    private Boolean deviceOnline;

    private Double batteryLevel;

    private Integer signalStrength;

    private String status;

    private LocalDateTime readingTime;

    private Double gasConcentration;

    private Double gasDensity;

    private String gasQuality;

    private Double differentialPressure;

    private Boolean emergencyShutdown;

    private Boolean alarmActive;
    
    private Double voltage;

    private Double current;
}
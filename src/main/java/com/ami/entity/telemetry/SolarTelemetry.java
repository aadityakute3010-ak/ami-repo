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
        name = "solar_telemetry",
        indexes = {

                @Index(
                        name = "idx_solar_device_id",
                        columnList = "deviceId"),

                @Index(
                        name = "idx_solar_reading_time",
                        columnList = "readingTime"),

                @Index(
                        name = "idx_solar_online",
                        columnList = "deviceOnline"),

                @Index(
                        name = "idx_solar_status",
                        columnList = "status"),

                @Index(
                        name = "idx_solar_alarm",
                        columnList = "alarmActive"),

                @Index(
                        name = "idx_solar_emergency",
                        columnList = "emergencyShutdown")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolarTelemetry extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String deviceId;

    private Double voltage;

    private Double current;

    private Double temperature;

    private Double consumption;

    private Boolean deviceOnline;

    private Double batteryLevel;

    private Integer signalStrength;

    private String status;

    private LocalDateTime readingTime;

    private Boolean emergencyShutdown;

    private Boolean alarmActive;
   
}
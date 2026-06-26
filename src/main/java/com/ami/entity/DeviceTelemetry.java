package com.ami.entity;

import java.time.LocalDateTime;

import com.ami.enums.SourceType;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "device_telemetry")
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

    private Double flowRate;

    private Double pressure;

    private Double temperature;

    private Double consumption;

    private Boolean leakDetected;

    private Boolean deviceOnline;
    
    private Double batteryLevel;

    private String valveStatus;

    private Double pipelineHealthScore;

    private Double sensorHealthScore;

    private String status;

    private LocalDateTime readingTime;
}
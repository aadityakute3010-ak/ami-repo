package com.ami.dto.responses;

import java.time.LocalDateTime;

import com.ami.enums.DeviceStatus;
import com.ami.enums.ProtocolType;
import com.ami.enums.SourceType;
import com.ami.enums.TechnologyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.ami.enums.DeviceHealthStatus;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceResponseDto {

    private Long id;

    private String deviceId;

    private String deviceName;
    
    private String meterName;

    private TechnologyType technologyType;

    private SourceType sourceType;

    private String macAddress;

    private String serialNumber;

    private String timezone;

    private Integer dataSampleCount;

    private String wakeupTime;

    private String firmwareVersion;

    private ProtocolType protocolType;

    private Boolean otaUpdatesEnabled;

    private DeviceStatus status;
    
    private DeviceHealthStatus healthStatus;

    private Boolean active;

    private Boolean online;

    private String assignedAdminName;

    private String assignedUserName;

    private LocalDateTime createdAt;
    
    private String customerName;

    private String customerAddress;

    private String buildingOrWing;

    private String area;

    private String zone;

    private String city;

    private String state;

    private String meterLocation;
    
    private Double meterStartReading;

    private String meterType;

    private String application;

    private String diameterSize;

    private Double literPerPulse;

    private String ctRatio;

    private String ptRatio;

    private String voltageClass;

    private String inverterType;

    private String plantCapacity;

    private Integer panelCount;
} 
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

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceResponseDto {

    private Long id;

    private String deviceId;

    private String deviceName;

    private TechnologyType technologyType;

    private SourceType sourceType;

    private String macAddress;

    private String serialNumber;

    private String timezone;

    private Integer sampleCount;

    private String wakeupTime;

    private String firmwareVersion;

    private ProtocolType protocolType;

    private Boolean otaUpdatesEnabled;

    private DeviceStatus status;

    private Boolean active;

    private Boolean online;

    private String assignedAdminName;

    private String assignedUserName;

    private LocalDateTime createdAt;
} 
package com.ami.dto.responses;

import com.ami.enums.DeviceStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GasDeviceResponseDto {

    private Long id;

    private String deviceId;

    private String deviceName;

    private String serialNumber;

    private String deviceType;

    private String manufacturer;

    private String firmwareVersion;

    private String location;

    private String zoneName;

    private Double latitude;

    private Double longitude;

    private DeviceStatus status;

    private Boolean active;
}
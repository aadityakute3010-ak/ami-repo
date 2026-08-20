package com.ami.dto.requests;

import com.ami.enums.DeviceStatus;

import lombok.Data;

@Data
public class UpdateGasDeviceRequestDto {

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
package com.ami.dto.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DeviceInfoDto {

    @NotBlank
    private String deviceId;

    @NotBlank
    private String deviceName;

    @NotBlank
    private String macAddress;

    @NotBlank
    private String serialNumber;
} 
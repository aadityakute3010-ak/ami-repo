package com.ami.dto.requests;

import lombok.Data;

@Data 
public class UpdateDeviceInfoDto {

    private String deviceId;

    private String deviceName;

    private String macAddress;

    private String serialNumber;
}

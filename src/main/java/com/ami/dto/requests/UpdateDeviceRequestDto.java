package com.ami.dto.requests;

import lombok.Data;

@Data
public class UpdateDeviceRequestDto {

    private UpdateDeviceInfoDto device;

    private UpdateMeterInfoDto meter;

    private CommunicationSettingsDto communication;

    private CustomerInfoDto customer;
}
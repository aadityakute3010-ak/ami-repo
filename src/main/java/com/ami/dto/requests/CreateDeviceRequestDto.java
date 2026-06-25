package com.ami.dto.requests;

import jakarta.validation.Valid;
import lombok.Data; 

@Data
public class CreateDeviceRequestDto {

    @Valid
    private DeviceInfoDto device;

    @Valid
    private MeterInfoDto meter;
    
    @Valid
    private CommunicationSettingsDto communication;

    @Valid
    private CustomerInfoDto customer; 
}   
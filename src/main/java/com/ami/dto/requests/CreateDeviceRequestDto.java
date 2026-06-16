package com.ami.dto.requests;

import com.ami.enums.BillingType;
import jakarta.validation.Valid;
import lombok.Data; 

@Data
public class CreateDeviceRequestDto {

    @Valid
    private DeviceInfoDto device;

    @Valid
    private MeterInfoDto meter;

    @Valid
    private CustomerInfoDto customer;

    private BillingType billingType;
}  
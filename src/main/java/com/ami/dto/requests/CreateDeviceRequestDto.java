package com.ami.dto.requests;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateDeviceRequestDto {

    @Valid
    @NotNull(message = "Device information is required")
    private DeviceInfoDto device;

    @Valid
    @NotNull(message = "Meter information is required")
    private MeterInfoDto meter;

    @Valid
    @NotNull(message = "Customer information is required")
    private CustomerInfoDto customer;

    @Valid
    private CommunicationSettingsDto communication;
}
package com.ami.dto.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateDeviceLocationRequestDto {

    @NotBlank
    private String meterLocation;

    private String buildingOrWing;

    private String area;

    private String zone;
} 
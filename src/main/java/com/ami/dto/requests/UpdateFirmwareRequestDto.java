package com.ami.dto.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateFirmwareRequestDto {

    @NotBlank(message = "Firmware version is required")
    private String firmwareVersion;

    private String remarks;
}
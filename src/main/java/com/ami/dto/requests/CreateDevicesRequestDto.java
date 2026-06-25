package com.ami.dto.requests;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CreateDevicesRequestDto {

    private Long assignedAdminId; // optional

    private Long assignedUserId; // optional

    @NotEmpty(message = "At least one device is required")
    @Valid 
    private List<CreateDeviceRequestDto> devices; 
} 
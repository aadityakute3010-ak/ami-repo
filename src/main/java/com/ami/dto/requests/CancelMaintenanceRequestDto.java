package com.ami.dto.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CancelMaintenanceRequestDto {

    @NotBlank(message = "Cancellation reason is required")
    private String reason;
}
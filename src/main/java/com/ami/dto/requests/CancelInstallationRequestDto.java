package com.ami.dto.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CancelInstallationRequestDto {

    @NotBlank(message = "Cancellation reason is required")
    private String reason;

}
package com.ami.dto.requests;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
@Data
public class RescheduleInstallationRequestDto {

    @NotNull(message = "Scheduled date is required")
    private LocalDateTime scheduledDate;

    private String remarks;
}
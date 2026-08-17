package com.ami.dto.requests;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RescheduleMaintenanceRequestDto {

    @NotNull(message = "New scheduled date is required")
    @Future(message = "New scheduled date must be in the future")
    private LocalDateTime scheduledAt;

    @NotBlank(message = "Reschedule reason is required")
    private String reason;
}
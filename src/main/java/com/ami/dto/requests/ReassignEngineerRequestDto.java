package com.ami.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReassignEngineerRequestDto {

    @NotNull(message = "Engineer ID is required")
    private Long engineerId;

    @NotBlank(message = "Reassignment reason is required")
    private String reason;
}
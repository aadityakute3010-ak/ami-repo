package com.ami.dto.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateMaintenanceRemarksRequestDto {

    @NotBlank(message = "Remarks cannot be empty")
    private String remarks;
}
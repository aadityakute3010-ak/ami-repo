package com.ami.dto.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignmentFailureRequestDto {

    @NotBlank(message = "Assignment failure reason is required")
    private String reason;
}
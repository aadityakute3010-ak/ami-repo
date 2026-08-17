package com.ami.dto.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignEngineerRequestDto {

    @NotNull
    private Long engineerId;
}
package com.ami.dto.requests;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignInstallationEngineerRequestDto {

    @NotNull(message = "Engineer ID is required")
    private Long engineerId;

}
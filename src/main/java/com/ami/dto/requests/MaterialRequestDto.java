package com.ami.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaterialRequestDto {

    @NotBlank
    private String materialName;

    @NotNull
    private Double quantity;

    private String unit;

    private Double cost;

    private String remarks;
}
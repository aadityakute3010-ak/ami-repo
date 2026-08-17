package com.ami.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IssueMaterialResponseDto {

    private Long id;

    private String materialName;

    private Double quantity;

    private String unit;

    private Double cost;

    private String remarks;
}
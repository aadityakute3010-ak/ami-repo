package com.ami.dto.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MaterialUsedResponseDto {

    private Long id;

    private String materialName;

    private Integer quantity;

    private String unit;

    private String remarks;
}
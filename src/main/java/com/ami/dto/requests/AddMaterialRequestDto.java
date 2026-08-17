package com.ami.dto.requests;

import lombok.Data;

@Data
public class AddMaterialRequestDto {

    private String materialName;

    private Integer quantity;

    private String unit;

    private String remarks;
}

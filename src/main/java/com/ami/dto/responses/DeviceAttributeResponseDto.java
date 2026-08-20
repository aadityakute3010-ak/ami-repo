package com.ami.dto.responses;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DeviceAttributeResponseDto {

    private Long id;

    private Long attributeKeyId;

    private String attributeKey;
    
    private String category;

    private String attributeValue;
}  
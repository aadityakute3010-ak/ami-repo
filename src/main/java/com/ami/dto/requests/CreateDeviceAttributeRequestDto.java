package com.ami.dto.requests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateDeviceAttributeRequestDto {

    private Long attributeKeyId;

    private String attributeValue;
}
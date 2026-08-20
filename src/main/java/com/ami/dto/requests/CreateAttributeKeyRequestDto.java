package com.ami.dto.requests;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
public class CreateAttributeKeyRequestDto {

    private String keyName;

    private String unit;
    
    private String category; 
}
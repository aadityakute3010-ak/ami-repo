package com.ami.dto.requests;

import lombok.Data;

@Data
public class UpdateSettingsRequestDto {

    private String provider;

    private String model;

    private Double temperature;

    private Integer maxTokens;

    private Boolean enabled;
}
package com.ami.dto.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SettingsResponseDto {

    private Long id;

    private String provider;

    private String model;

    private Double temperature;

    private Integer maxTokens;

    private Boolean enabled;
}
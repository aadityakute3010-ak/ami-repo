package com.ami.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TelemetryResponseDto {

    private Long deviceId;
    private String deviceCode;
    private String sourceType;

    private Object telemetryData;
}
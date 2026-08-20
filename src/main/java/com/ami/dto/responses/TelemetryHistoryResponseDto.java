package com.ami.dto.responses;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TelemetryHistoryResponseDto {

    private Long deviceId;

    private String deviceCode;

    private String sourceType;

    private List<?> telemetryRecords;
}
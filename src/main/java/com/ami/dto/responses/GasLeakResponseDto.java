package com.ami.dto.responses;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GasLeakResponseDto {

    private String deviceId;

    private Boolean leakDetected;

    private String leakSeverity;

    private String leakLocation;

    private LocalDateTime readingTime;

}
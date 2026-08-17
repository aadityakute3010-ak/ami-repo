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
public class GasHistoryResponseDto {

    private String deviceId;

    private Double pressure;

    private Double flowRate;

    private Double consumption;

    private Double gasConcentration;

    private Boolean leakDetected;

    private String status;

    private LocalDateTime readingTime;

}
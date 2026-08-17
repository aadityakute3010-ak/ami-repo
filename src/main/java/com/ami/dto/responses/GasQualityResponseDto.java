package com.ami.dto.responses;

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
public class GasQualityResponseDto {

    private String deviceId;

    private String gasQuality;

    private Double gasDensity;

    private Double gasConcentration;

    private Double pressure;

}
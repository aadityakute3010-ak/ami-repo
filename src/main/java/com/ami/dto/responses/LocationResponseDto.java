package com.ami.dto.responses;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationResponseDto {

    private String deviceId;

    private String deviceName;

    private String location;

    private String zone;

    private Double latitude;

    private Double longitude;

    private Boolean online;
}
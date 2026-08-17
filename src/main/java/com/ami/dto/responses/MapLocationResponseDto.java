package com.ami.dto.responses;

import com.ami.enums.SourceType;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MapLocationResponseDto {

    private String deviceId;

    private String deviceName;

    private SourceType sourceType;

    private String zone;

    private String location;

    private Double latitude;

    private Double longitude;

    private Boolean online;
}
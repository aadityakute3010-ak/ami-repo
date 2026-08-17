package com.ami.dto.responses;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigurationResponseDto {

    private String deviceId;

    private Integer sampleCount;

    private String wakeupTime;

    private String timezone;

    private Boolean otaUpdatesEnabled;

    private String protocolType;

    private String firmwareVersion;
}
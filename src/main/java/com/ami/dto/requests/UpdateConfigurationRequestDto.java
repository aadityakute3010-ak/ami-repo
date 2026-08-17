package com.ami.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateConfigurationRequestDto {

    private Integer sampleCount;

    private String wakeupTime;

    private String timezone;

    private Boolean otaUpdatesEnabled;

    private String protocolType;
}
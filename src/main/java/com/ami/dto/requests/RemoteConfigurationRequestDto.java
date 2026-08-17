package com.ami.dto.requests;

import lombok.Data;

@Data
public class RemoteConfigurationRequestDto {

    private String configuration;

    private String requestedBy;
}
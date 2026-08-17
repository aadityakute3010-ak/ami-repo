package com.ami.dto.responses;

import java.time.LocalDateTime;

import com.ami.enums.ConfigurationStatus;
import com.ami.enums.ConfigurationType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdministrationConfigurationResponseDto {

    private Long id;

    private ConfigurationType configurationType;

    private ConfigurationStatus status;

    private String configurationName;

    private String configurationValue;

    private String updatedBy;

    private LocalDateTime updatedAt;

    private String remarks;
}
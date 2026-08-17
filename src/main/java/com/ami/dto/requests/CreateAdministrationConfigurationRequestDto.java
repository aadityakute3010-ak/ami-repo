package com.ami.dto.requests;

import com.ami.enums.ConfigurationType;

import lombok.Data;

@Data
public class CreateAdministrationConfigurationRequestDto {

    private ConfigurationType configurationType;

    private String configurationName;

    private String configurationValue;

    private String updatedBy;

    private String remarks;
}
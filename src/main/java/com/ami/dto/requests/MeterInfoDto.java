package com.ami.dto.requests;

import com.ami.enums.AmiApplicationType;
import com.ami.enums.ApplicationOfAmi;
import com.ami.enums.DiameterSize;
import com.ami.enums.SourceType;
import com.ami.enums.TechnologyType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MeterInfoDto {

    @NotBlank
    private String meterName;

    @NotNull
    private SourceType sourceType;

    @NotNull
    private TechnologyType technologyType;

    @NotNull
    private ApplicationOfAmi applicationOfAmi;

    @NotNull
    private AmiApplicationType amiApplicationType;

    @NotNull
    private DiameterSize diameterSize;

    private Double literPerPulse;

    private Double meterStartReading;
} 
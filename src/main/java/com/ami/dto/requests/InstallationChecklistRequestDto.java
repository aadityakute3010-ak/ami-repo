package com.ami.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstallationChecklistRequestDto {

    private Boolean meterMounted;

    private Boolean wiringCompleted;

    private Boolean communicationVerified;

    private Boolean meterActivated;

    private Boolean readingVerified;

    private Boolean customerVerified;

}
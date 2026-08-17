package com.ami.dto.responses;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstallationChecklistResponseDto {

    private Long id;

    private Boolean meterMounted;

    private Boolean wiringCompleted;

    private Boolean communicationVerified;

    private Boolean meterActivated;

    private Boolean readingVerified;

    private Boolean customerVerified;

    private Boolean mandatory;

    private String checkedBy;

    private LocalDateTime checkedAt;

    private String remarks;
}
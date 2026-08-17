package com.ami.dto.responses;

import java.time.LocalDateTime;

import com.ami.enums.InstallationStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstallationAssignmentAttemptResponseDto {

    private Long id;

    private Long installationId;

    private String installationNumber;

    private Long engineerId;

    private String engineerName;

    private Boolean successful;

    private String failureReason;

    private InstallationStatus installationStatus;

    private String assignedBy;

    private LocalDateTime attemptedAt;

}
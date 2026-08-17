package com.ami.dto.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InstallationEngineerWorkloadResponseDto {

    private Long engineerId;

    private String engineerName;

    private Long assignedInstallations;

    private Long inProgressInstallations;

    private Long completedInstallations;

    private Long cancelledInstallations;

}
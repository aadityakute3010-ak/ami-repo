package com.ami.service;

import com.ami.dto.responses.InstallationResponseDto;

public interface RetryInstallationAssignmentService {

    InstallationResponseDto retryAssignment(
            Long installationId);

}
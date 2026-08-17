package com.ami.service;

import com.ami.dto.responses.InstallationResponseDto;

public interface InstallationAutoAssignmentService {

    InstallationResponseDto autoAssignEngineer(
            Long installationId);

}
package com.ami.service;

import java.util.List;

import com.ami.dto.requests.AssignInstallationEngineerRequestDto;
import com.ami.dto.requests.InstallationAssignmentFailureRequestDto;
import com.ami.dto.requests.ReassignInstallationEngineerRequestDto;
import com.ami.dto.responses.InstallationAssignmentAttemptResponseDto;
import com.ami.dto.responses.InstallationResponseDto;

public interface InstallationAssignmentService {

    InstallationResponseDto assignEngineer(
            Long installationId,
            AssignInstallationEngineerRequestDto request);

    InstallationResponseDto reassignEngineer(
            Long installationId,
            ReassignInstallationEngineerRequestDto request);

    InstallationResponseDto markAssignmentFailed(
            Long installationId,
            InstallationAssignmentFailureRequestDto request);

    List<InstallationAssignmentAttemptResponseDto>
    getAssignmentAttempts(
            Long installationId);

    List<InstallationResponseDto>
    getEngineerAssignments(
            Long engineerId);
    
    
}
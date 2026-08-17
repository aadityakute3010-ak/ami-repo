package com.ami.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ami.dto.requests.AssignInstallationEngineerRequestDto;
import com.ami.dto.requests.InstallationAssignmentFailureRequestDto;
import com.ami.dto.requests.ReassignInstallationEngineerRequestDto;
import com.ami.dto.responses.InstallationAssignmentAttemptResponseDto;
import com.ami.dto.responses.InstallationResponseDto;
import com.ami.service.InstallationAssignmentService;
import com.ami.service.InstallationService;

@Service
public class InstallationAssignmentServiceImpl
        implements InstallationAssignmentService {

    private final InstallationService installationService;

    public InstallationAssignmentServiceImpl(
            InstallationService installationService) {

        this.installationService =
                installationService;
    }

    @Override
    public InstallationResponseDto assignEngineer(
            Long installationId,
            AssignInstallationEngineerRequestDto request) {

        return installationService.assignEngineer(
                installationId,
                request);
    }

    @Override
    public InstallationResponseDto reassignEngineer(
            Long installationId,
            ReassignInstallationEngineerRequestDto request) {

        return installationService.reassignEngineer(
                installationId,
                request);
    }

    @Override
    public InstallationResponseDto markAssignmentFailed(
            Long installationId,
            InstallationAssignmentFailureRequestDto request) {

        return installationService.markAssignmentFailed(
                installationId,
                request);
    }

    @Override
    public List<InstallationAssignmentAttemptResponseDto>
    getAssignmentAttempts(
            Long installationId) {

        return installationService.getAssignmentAttempts(
                installationId);
    }

    @Override
    public List<InstallationResponseDto>
    getEngineerAssignments(
            Long engineerId) {

        return installationService.getEngineerAssignments(
                engineerId);
    }
}
package com.ami.service.impl;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ami.dto.requests.AssignInstallationEngineerRequestDto;
import com.ami.dto.responses.InstallationResponseDto;
import com.ami.entity.Installation;
import com.ami.entity.InstallationAssignmentAttempt;
import com.ami.entity.User;
import com.ami.enums.EngineerAttendanceStatus;
import com.ami.enums.EngineerAvailabilityStatus;
import com.ami.enums.InstallationStatus;
import com.ami.enums.RoleType;
import com.ami.repository.InstallationAssignmentAttemptRepository;
import com.ami.repository.InstallationHistoryRepository;
import com.ami.repository.InstallationRepository;
import com.ami.repository.InstallationTimelineRepository;
import com.ami.repository.UserRepository;
import com.ami.service.AuditService;
import com.ami.service.InstallationAutoAssignmentService;
import com.ami.service.InstallationNotificationService;
import com.ami.service.InstallationService;
import com.ami.service.InstallationWorkloadService;

@Service
public class InstallationAutoAssignmentServiceImpl
        implements InstallationAutoAssignmentService {

    private final InstallationRepository installationRepository;

    private final UserRepository userRepository;

    private final InstallationService installationService;

    private final InstallationNotificationService
            installationNotificationService;
    
    private final InstallationWorkloadService
    installationWorkloadService;

    private final InstallationAssignmentAttemptRepository
            installationAssignmentAttemptRepository;

    public InstallationAutoAssignmentServiceImpl(

            InstallationRepository installationRepository,

            UserRepository userRepository,

            InstallationService installationService,

            InstallationNotificationService
                    installationNotificationService,

            InstallationAssignmentAttemptRepository
                    installationAssignmentAttemptRepository,

            InstallationTimelineRepository
                    installationTimelineRepository,

            InstallationHistoryRepository
                    installationHistoryRepository,

            AuditService auditService,

            InstallationWorkloadService
                    installationWorkloadService) {

        this.installationRepository =
                installationRepository;

        this.userRepository =
                userRepository;

        this.installationService =
                installationService;

        this.installationNotificationService =
                installationNotificationService;

        this.installationAssignmentAttemptRepository =
                installationAssignmentAttemptRepository;
        this.installationWorkloadService =
                installationWorkloadService;
    }
    @Override
    public InstallationResponseDto autoAssignEngineer(
            Long installationId) {

        Installation installation =
                installationRepository
                        .findById(installationId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Installation not found"));

        List<User> engineers =
                userRepository
                        .findByRoleAndAvailabilityStatus(

                                RoleType.SERVICE_ENGINEER,

                                EngineerAvailabilityStatus.AVAILABLE)

                        .stream()

                        .filter(e ->
                                e.getAttendanceStatus()
                                        == EngineerAttendanceStatus.PRESENT)

                        .toList();

        if (engineers.isEmpty()) {

            throw new RuntimeException(
                    "No available engineers");
        }

        User bestEngineer =
                installationWorkloadService
                        .getBestEngineer(
                                engineers);

        AssignInstallationEngineerRequestDto request =
                AssignInstallationEngineerRequestDto
                        .builder()
                        .engineerId(bestEngineer.getId())
                        .build();

        InstallationResponseDto response =
                installationService.assignEngineer(
                        installationId,
                        request);

        recordAssignmentAttempt(
                installation,
                bestEngineer);

        installationNotificationService
                .sendInstallationNotification(

                        installation.getId(),

                        installation.getInstallationNumber(),

                        "Auto Assignment",

                        "Automatically assigned to "
                                + bestEngineer.getFirstName(),

                        "AUTO_ASSIGN",

                        "SYSTEM");

        return response;
    }
    
    private void recordAssignmentAttempt(
            Installation installation,
            User engineer) {

        InstallationAssignmentAttempt attempt =

                InstallationAssignmentAttempt.builder()

                        .installation(installation)

                        .engineer(engineer)

                        .successful(true)

                        .installationStatus(
                                InstallationStatus.ASSIGNED)

                        .assignedBy("AUTO_ASSIGNMENT_ENGINE")

                        .build();

        installationAssignmentAttemptRepository
                .save(attempt);
    }
}
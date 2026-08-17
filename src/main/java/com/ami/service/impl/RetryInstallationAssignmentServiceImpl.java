package com.ami.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import java.util.Comparator;
import com.ami.dto.requests.AssignInstallationEngineerRequestDto;
import com.ami.dto.requests.CreateAuditLogRequestDto;
import com.ami.dto.responses.InstallationResponseDto;
import com.ami.entity.Installation;
import com.ami.entity.InstallationAssignmentAttempt;
import com.ami.entity.InstallationHistory;
import com.ami.entity.InstallationTimeline;
import com.ami.entity.User;
import com.ami.enums.EngineerAttendanceStatus;
import com.ami.enums.EngineerAvailabilityStatus;
import com.ami.enums.HistoryStatus;
import com.ami.enums.InstallationStatus;
import com.ami.enums.InstallationTimelineEvent;
import com.ami.enums.RoleType;
import com.ami.repository.InstallationAssignmentAttemptRepository;
import com.ami.repository.InstallationHistoryRepository;
import com.ami.repository.InstallationRepository;
import com.ami.repository.InstallationTimelineRepository;
import com.ami.repository.UserRepository;
import com.ami.service.AuditService;
import com.ami.service.InstallationNotificationService;
import com.ami.service.InstallationService;
import com.ami.service.InstallationWorkloadService;
import com.ami.service.RetryInstallationAssignmentService;

@Service
public class RetryInstallationAssignmentServiceImpl
        implements RetryInstallationAssignmentService {

	
	private static final int MAX_RETRY_COUNT = 3;
    private final InstallationRepository installationRepository;

    private final UserRepository userRepository;

    private final InstallationService installationService;

    private final InstallationNotificationService installationNotificationService;

    private final InstallationAssignmentAttemptRepository
            installationAssignmentAttemptRepository;
    
    private final InstallationWorkloadService
    installationWorkloadService;
    
    private final InstallationTimelineRepository
    installationTimelineRepository;

private final InstallationHistoryRepository
    installationHistoryRepository;

private final AuditService
    auditService;

    public RetryInstallationAssignmentServiceImpl(

            InstallationRepository installationRepository,

            UserRepository userRepository,

            InstallationService installationService,

            InstallationNotificationService installationNotificationService,

            InstallationAssignmentAttemptRepository installationAssignmentAttemptRepository,
            InstallationTimelineRepository installationTimelineRepository,

            InstallationHistoryRepository installationHistoryRepository,

            AuditService auditService,
            
            InstallationWorkloadService
            installationWorkloadService) {

        this.installationRepository = installationRepository;

        this.userRepository = userRepository;

        this.installationService = installationService;

        this.installationNotificationService =
                installationNotificationService;

        this.installationAssignmentAttemptRepository =
                installationAssignmentAttemptRepository;
        
        this.installationTimelineRepository =
                installationTimelineRepository;

        this.installationHistoryRepository =
                installationHistoryRepository;

        this.auditService =
                auditService;
        this.installationWorkloadService =
                installationWorkloadService;
    }
    @Override
    public InstallationResponseDto retryAssignment(
            Long installationId) {

        Installation installation =
                installationRepository
                        .findById(installationId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Installation not found"));
        
        long retryCount =
                getRetryCount(installationId);

        if (retryCount >= MAX_RETRY_COUNT) {

            installation.setStatus(
                    InstallationStatus.CANCELLED);

            installationRepository.save(
                    installation);

            addTimeline(
                    installation,
                    InstallationTimelineEvent.RETRY_LIMIT_REACHED,
                    "Maximum retry attempts reached.",
                    "RETRY_ENGINE");

            addHistory(
                    installation,
                    "AUTO_ESCALATED",
                    "Retry limit exceeded.",
                    "FAILED",
                    "RETRY_ENGINE");
            addAuditLog(
                    "INSTALLATION",
                    installation.getId(),
                    "AUTO_ESCALATED",
                    "SYSTEM",
                    "Maximum retry limit reached.");

            installationNotificationService
                    .sendInstallationNotification(

                            installation.getId(),

                            installation.getInstallationNumber(),

                            "Installation Escalated",

                            "Maximum retry limit reached.",

                            "AUTO_ESCALATED",

                            "SYSTEM");

            throw new RuntimeException(
                    "Retry limit reached. Installation escalated.");
        }

        User nextEngineer =
                findNextEngineer(installation);

        if (nextEngineer == null) {

            throw new RuntimeException(
                    "No engineer available for retry assignment");
        }

        AssignInstallationEngineerRequestDto request =
                AssignInstallationEngineerRequestDto
                        .builder()
                        .engineerId(nextEngineer.getId())
                        .build();

        InstallationResponseDto response =
                installationService.assignEngineer(
                        installationId,
                        request);

        recordRetryAttempt(
                installation,
                nextEngineer);
        
        addTimeline(
                installation,
                InstallationTimelineEvent.RETRY_ASSIGNMENT,
                "Installation reassigned automatically after failure.",
                nextEngineer.getFirstName()
                        + " "
                        + nextEngineer.getLastName());
        addHistory(
                installation,
                "RETRY_ASSIGNMENT",
                "Installation assigned using retry engine.",
                "SUCCESS",
                "RETRY_ENGINE");
        addAuditLog(

                "INSTALLATION",

                installation.getId(),

                "RETRY_ASSIGNMENT",

                "SYSTEM",

                "Installation automatically reassigned.");

        installationNotificationService
                .sendInstallationNotification(

                        installation.getId(),

                        installation.getInstallationNumber(),

                        "Retry Assignment",

                        "Installation reassigned to "
                                + nextEngineer.getFirstName()
                                + " "
                                + nextEngineer.getLastName(),

                        "RETRY_ASSIGNMENT",

                        "SYSTEM");

        return response;
    }
    private User findNextEngineer(
            Installation installation) {

        /*
         * Get all previous assignment attempts
         */
        List<Long> attemptedEngineerIds =
                installationAssignmentAttemptRepository
                        .findByInstallationIdOrderByAttemptedAtDesc(
                                installation.getId())
                        .stream()
                        .filter(attempt -> attempt.getEngineer() != null)
                        .map(attempt -> attempt.getEngineer().getId())
                        .toList();

        /*
         * Get available engineers
         */
        List<User> availableEngineers =
                userRepository
                        .findByRoleAndAvailabilityStatus(
                                RoleType.SERVICE_ENGINEER,
                                EngineerAvailabilityStatus.AVAILABLE)
                        .stream()

                        // Engineer must be present
                        .filter(engineer ->
                                engineer.getAttendanceStatus()
                                        == EngineerAttendanceStatus.PRESENT)

                        // Engineer must not have been tried already
                        .filter(engineer ->
                                !attemptedEngineerIds.contains(
                                        engineer.getId()))

                        .toList();

        if (availableEngineers.isEmpty()) {
            return null;
        }

        /*
         * Choose engineer with least workload
         */
        return installationWorkloadService
                .getBestEngineer(
                        availableEngineers);
        
    }
    
    private void recordRetryAttempt(
            Installation installation,
            User engineer) {

        InstallationAssignmentAttempt attempt =

                InstallationAssignmentAttempt.builder()

                        .installation(installation)

                        .engineer(engineer)

                        .successful(true)

                        .installationStatus(
                                InstallationStatus.ASSIGNED)

                        .assignedBy("RETRY_ENGINE")

                        .failureReason(
                                "Retry Attempt #"
                                        + (getRetryCount(
                                                installation.getId()) + 1))

                        .build();

        installationAssignmentAttemptRepository
                .save(attempt);
    }
    private void addTimeline(
            Installation installation,
            InstallationTimelineEvent event,
            String description,
            String performedBy) {

        InstallationTimeline timeline =
                InstallationTimeline.builder()
                        .installation(installation)
                        .event(event)
                        .description(description)
                        .performedBy(performedBy)
                        .build();

        installationTimelineRepository.save(timeline);

        installation.getTimeline().add(timeline);
    }
    private void addHistory(
            Installation installation,
            String action,
            String description,
            String status,
            String performedBy) {

        InstallationHistory history =
                InstallationHistory.builder()
                        .installation(installation)
                        .action(action)
                        .description(description)
                        .status(status)
                        .performedBy(performedBy)
                        .build();

        installationHistoryRepository.save(history);

        installation.getHistory().add(history);
    }
    private void addAuditLog(
            String module,
            Long entityId,
            String action,
            String performedBy,
            String description) {

        auditService.createAuditLog(

                CreateAuditLogRequestDto.builder()

                        .module(module)

                        .entityId(entityId)

                        .action(action)

                        .performedBy(performedBy)

                        .description(description)

                        .build());
    }
    private long getRetryCount(
            Long installationId) {

        return installationAssignmentAttemptRepository
                .findByInstallationIdOrderByAttemptedAtDesc(
                        installationId)
                .size();
    }
}
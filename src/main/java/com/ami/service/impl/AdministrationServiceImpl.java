package com.ami.service.impl;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.ami.dto.requests.CreateAdministrationConfigurationRequestDto;
import com.ami.dto.responses.AdministrationConfigurationResponseDto;
import com.ami.dto.responses.AdministrationDashboardResponseDto;
import com.ami.dto.responses.AuditLogResponseDto;
import com.ami.dto.responses.ConfigurationHistoryResponseDto;
import com.ami.dto.responses.VersionHistoryResponseDto;
import com.ami.entity.AdministrationConfiguration;
import com.ami.entity.ConfigurationHistory;
import com.ami.enums.ConfigurationStatus;
import com.ami.enums.ConfigurationType;
import com.ami.repository.AdministrationConfigurationRepository;
import com.ami.repository.AuditLogRepository;
import com.ami.repository.ConfigurationHistoryRepository;
import com.ami.service.AdministrationService;
import com.ami.service.NotificationManagementService;
import com.ami.service.NotificationManagementService;
import com.ami.dto.requests.CreateNotificationRequestDto;
import com.ami.enums.NotificationType;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdministrationServiceImpl
        implements AdministrationService {

    private final AdministrationConfigurationRepository
            administrationConfigurationRepository;
    
    private final ConfigurationHistoryRepository
    configurationHistoryRepository;
    
    private final AuditLogRepository auditLogRepository;
    
    private final NotificationManagementService
    notificationManagementService;
    
    @Override
    public AdministrationConfigurationResponseDto
    createConfiguration(
            CreateAdministrationConfigurationRequestDto request) {

        AdministrationConfiguration configuration =

                AdministrationConfiguration.builder()

                        .configurationType(
                                request.getConfigurationType())

                        .status(
                                ConfigurationStatus.ACTIVE)

                        .configurationName(
                                request.getConfigurationName())

                        .configurationValue(
                                request.getConfigurationValue())

                        .updatedBy(
                                request.getUpdatedBy())

                        .updatedAt(
                                LocalDateTime.now())

                        .remarks(
                                request.getRemarks())

                        .build();

        configuration =
                administrationConfigurationRepository.save(
                        configuration);

        notificationManagementService.createNotification(

                CreateNotificationRequestDto.builder()

                        .type(NotificationType.ADMINISTRATION)

                        .title("Configuration Created")

                        .message(
                                "Configuration "
                                        + configuration.getConfigurationName()
                                        + " created")

                        .recipient(
                                configuration.getUpdatedBy())

                        .build());

        return mapToResponse(
                configuration);
    }
    @Override
    public AdministrationConfigurationResponseDto
    getConfigurationById(
            Long id) {

        AdministrationConfiguration configuration =

                administrationConfigurationRepository

                        .findById(id)

                        .orElseThrow(() ->

                                new RuntimeException(
                                        "Configuration not found"));

        return mapToResponse(
                configuration);
    }
    @Override
    public Page<AdministrationConfigurationResponseDto>
    getAllConfigurations(

            int page,

            int size,

            String search,

            ConfigurationType configurationType,

            ConfigurationStatus status,

            String sortBy,

            String direction) {

        Sort sort = direction.equalsIgnoreCase("DESC")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        sort);

        Specification<AdministrationConfiguration> specification =
                (root, query, cb) -> cb.conjunction();

        if (search != null && !search.isBlank()) {

            specification = specification.and((root, query, cb) ->

                    cb.or(

                            cb.like(

                                    cb.lower(root.get("configurationName")),

                                    "%" + search.toLowerCase() + "%"),

                            cb.like(

                                    cb.lower(root.get("configurationValue")),

                                    "%" + search.toLowerCase() + "%"),

                            cb.like(

                                    cb.lower(root.get("updatedBy")),

                                    "%" + search.toLowerCase() + "%")));
        }

        if (configurationType != null) {

            specification = specification.and((root, query, cb) ->

                    cb.equal(
                            root.get("configurationType"),
                            configurationType));
        }

        if (status != null) {

            specification = specification.and((root, query, cb) ->

                    cb.equal(
                            root.get("status"),
                            status));
        }

        return administrationConfigurationRepository

                .findAll(
                        specification,
                        pageable)

                .map(this::mapToResponse);
    }
    @Override
    public AdministrationConfigurationResponseDto
    updateConfiguration(

            Long id,

            CreateAdministrationConfigurationRequestDto request) {

        AdministrationConfiguration configuration =

                administrationConfigurationRepository

                        .findById(id)

                        .orElseThrow(() ->

                                new RuntimeException(
                                        "Configuration not found"));

        // Save history BEFORE updating the configuration
        ConfigurationHistory history =

                ConfigurationHistory.builder()

                        .configurationId(
                                configuration.getId())

                        .configurationName(
                                configuration.getConfigurationName())

                        .oldValue(
                                configuration.getConfigurationValue())

                        .newValue(
                                request.getConfigurationValue())

                        .updatedBy(
                                request.getUpdatedBy())

                        .updatedAt(
                                LocalDateTime.now())

                        .build();

        configurationHistoryRepository.save(
                history);

        // Update configuration
        configuration.setConfigurationType(
                request.getConfigurationType());

        configuration.setConfigurationName(
                request.getConfigurationName());

        configuration.setConfigurationValue(
                request.getConfigurationValue());

        configuration.setUpdatedBy(
                request.getUpdatedBy());

        configuration.setUpdatedAt(
                LocalDateTime.now());

        configuration.setRemarks(
                request.getRemarks());

        configuration =
                administrationConfigurationRepository.save(
                        configuration);
        
        notificationManagementService.createNotification(

                CreateNotificationRequestDto.builder()

                        .type(NotificationType.ADMINISTRATION)

                        .title("Configuration Updated")

                        .message(
                                "Configuration "
                                        + configuration.getConfigurationName()
                                        + " updated")

                        .recipient(
                                configuration.getUpdatedBy())

                        .build());

        return mapToResponse(
                configuration);
    }
    @Override
    public String deleteConfiguration(
            Long id) {

        AdministrationConfiguration configuration =

                administrationConfigurationRepository

                        .findById(id)

                        .orElseThrow(() ->

                                new RuntimeException(
                                        "Configuration not found"));

        notificationManagementService.createNotification(

                CreateNotificationRequestDto.builder()

                        .type(NotificationType.ADMINISTRATION)

                        .title("Configuration Deleted")

                        .message(
                                "Configuration "
                                        + configuration.getConfigurationName()
                                        + " deleted")

                        .recipient(
                                configuration.getUpdatedBy())

                        .build());

        administrationConfigurationRepository
                .delete(configuration);

        return "Configuration deleted successfully";
    }
    @Override
    public AdministrationDashboardResponseDto
    getDashboard() {

        List<AdministrationConfiguration> list =
                administrationConfigurationRepository.findAll();

        return AdministrationDashboardResponseDto

                .builder()

                .totalConfigurations(
                        (long) list.size())

                .activeConfigurations(
                        list.stream()
                                .filter(c ->
                                        c.getStatus() ==
                                        ConfigurationStatus.ACTIVE)
                                .count())

                .inactiveConfigurations(
                        list.stream()
                                .filter(c ->
                                        c.getStatus() ==
                                        ConfigurationStatus.INACTIVE)
                                .count())

                .pendingConfigurations(
                        list.stream()
                                .filter(c ->
                                        c.getStatus() ==
                                        ConfigurationStatus.PENDING)
                                .count())

                .deviceConfigurations(
                        list.stream()
                                .filter(c ->
                                        c.getConfigurationType() ==
                                        ConfigurationType.DEVICE)
                                .count())

                .alertConfigurations(
                        list.stream()
                                .filter(c ->
                                        c.getConfigurationType() ==
                                        ConfigurationType.ALERT)
                                .count())

                .thresholdConfigurations(
                        list.stream()
                                .filter(c ->
                                        c.getConfigurationType() ==
                                        ConfigurationType.THRESHOLD)
                                .count())

                .firmwareConfigurations(
                        list.stream()
                                .filter(c ->
                                        c.getConfigurationType() ==
                                        ConfigurationType.FIRMWARE)
                                .count())

                .communicationConfigurations(
                        list.stream()
                                .filter(c ->
                                        c.getConfigurationType() ==
                                        ConfigurationType.COMMUNICATION)
                                .count())

                .build();
    }
    
    @Override
    public List<ConfigurationHistoryResponseDto>
    getConfigurationHistory(
            Long configurationId) {

        return configurationHistoryRepository

                .findByConfigurationIdOrderByUpdatedAtDesc(
                        configurationId)

                .stream()

                .map(history ->

                        ConfigurationHistoryResponseDto

                                .builder()

                                .id(
                                        history.getId())

                                .configurationId(
                                        history.getConfigurationId())

                                .configurationName(
                                        history.getConfigurationName())

                                .oldValue(
                                        history.getOldValue())

                                .newValue(
                                        history.getNewValue())

                                .updatedBy(
                                        history.getUpdatedBy())

                                .updatedAt(
                                        history.getUpdatedAt())

                                .build())

                .toList();
    }
    @Override
    public List<VersionHistoryResponseDto>
    getVersionHistory(
            Long configurationId) {

        return configurationHistoryRepository

                .findByConfigurationIdOrderByUpdatedAtDesc(
                        configurationId)

                .stream()

                .map(history ->

                        VersionHistoryResponseDto

                                .builder()

                                .id(
                                        history.getId())

                                .configurationId(
                                        history.getConfigurationId())

                                .configurationName(
                                        history.getConfigurationName())

                                .oldValue(
                                        history.getOldValue())

                                .newValue(
                                        history.getNewValue())

                                .updatedBy(
                                        history.getUpdatedBy())

                                .updatedAt(
                                        history.getUpdatedAt())

                                .build())

                .toList();
    }
    @Override
    public List<AuditLogResponseDto>
    getAuditLogs(
            String module) {

        return auditLogRepository

                .findByModuleOrderByTimestampDesc(
                        module)

                .stream()

                .map(log ->

                        AuditLogResponseDto

                                .builder()

                                .id(
                                        log.getId())

                                .module(
                                        log.getModule())

                                .entityId(
                                        log.getEntityId())

                                .action(
                                        log.getAction())

                                .performedBy(
                                        log.getPerformedBy())

                                .description(
                                        log.getDescription())

                                .timestamp(
                                        log.getTimestamp())

                                .build())

                .toList();
    }
    private AdministrationConfigurationResponseDto
    mapToResponse(
            AdministrationConfiguration configuration) {

        return AdministrationConfigurationResponseDto
                .builder()

                .id(
                        configuration.getId())

                .configurationType(
                        configuration.getConfigurationType())

                .status(
                        configuration.getStatus())

                .configurationName(
                        configuration.getConfigurationName())

                .configurationValue(
                        configuration.getConfigurationValue())

                .updatedBy(
                        configuration.getUpdatedBy())

                .updatedAt(
                        configuration.getUpdatedAt())

                .remarks(
                        configuration.getRemarks())

                .build();
    }

}
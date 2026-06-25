package com.ami.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ami.dto.requests.CreateAuditLogRequestDto;
import com.ami.dto.responses.AuditLogResponseDto;
import com.ami.entity.AuditLog;
import com.ami.repository.AuditLogRepository;
import com.ami.service.AuditService;

@Service
public class AuditServiceImpl
        implements AuditService {

    private final AuditLogRepository auditLogRepository;

    public AuditServiceImpl(
            AuditLogRepository auditLogRepository) {

        this.auditLogRepository =
                auditLogRepository;
    }

    @Override
    public AuditLogResponseDto createAuditLog(
            CreateAuditLogRequestDto request) {

        AuditLog auditLog =
                AuditLog.builder()
                        .module(
                                request.getModule())
                        .entityId(
                                request.getEntityId())
                        .action(
                                request.getAction())
                        .performedBy(
                                request.getPerformedBy())
                        .description(
                                request.getDescription())
                        .timestamp(
                                LocalDateTime.now())
                        .build();

        auditLog =
                auditLogRepository.save(
                        auditLog);

        return mapToResponse(auditLog);
    }

    @Override
    public List<AuditLogResponseDto>
    getAllAuditLogs() {

        return auditLogRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<AuditLogResponseDto>
    getLogsByModule(
            String module) {

        return auditLogRepository
                .findByModule(module)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<AuditLogResponseDto>
    getLogsByEntityId(
            Long entityId) {

        return auditLogRepository
                .findByEntityId(entityId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private AuditLogResponseDto
    mapToResponse(
            AuditLog auditLog) {

        return AuditLogResponseDto
                .builder()
                .id(auditLog.getId())
                .module(
                        auditLog.getModule())
                .entityId(
                        auditLog.getEntityId())
                .action(
                        auditLog.getAction())
                .performedBy(
                        auditLog.getPerformedBy())
                .description(
                        auditLog.getDescription())
                .timestamp(
                        auditLog.getTimestamp())
                .build();
    }
}
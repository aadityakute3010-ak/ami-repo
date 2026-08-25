package com.ami.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.ami.dto.requests.CreateAuditLogRequestDto;
import com.ami.dto.responses.AuditDashboardResponseDto;
import com.ami.dto.responses.AuditLogResponseDto;
import com.ami.dto.responses.PagedAuditLogResponseDto;
import com.ami.entity.AuditLog;
import com.ami.entity.User;
import com.ami.enums.RoleType;
import com.ami.repository.AuditLogRepository;
import com.ami.security.SecurityUtils;
import com.ami.service.AuditService;
import com.ami.specification.AuditLogSpecifications;

@Service
public class AuditServiceImpl implements AuditService {

    private static final String BILLING_MODULE = "BILLING";

    private static final Set<String> DELETE_ACTIONS = Set.of("DELETED", "DEACTIVATED");

    private final AuditLogRepository auditLogRepository;

    private final SecurityUtils securityUtils;

    public AuditServiceImpl(AuditLogRepository auditLogRepository, SecurityUtils securityUtils) {
        this.auditLogRepository = auditLogRepository;
        this.securityUtils = securityUtils;
    }

    @Override
    public AuditLogResponseDto createAuditLog(CreateAuditLogRequestDto request) {

        AuditLog auditLog = AuditLog.builder()
                .module(request.getModule())
                .entityId(request.getEntityId())
                .entityType(request.getEntityType())
                .targetAdminId(request.getTargetAdminId())
                .action(request.getAction())
                .performedBy(request.getPerformedBy())
                .description(request.getDescription())
                .timestamp(LocalDateTime.now())
                .build();

        auditLog = auditLogRepository.save(auditLog);

        return mapToResponse(auditLog);
    }

    @Override
    public List<AuditLogResponseDto> getAllAuditLogs() {
        return auditLogRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    @Override
    public List<AuditLogResponseDto> getLogsByModule(String module) {
        return auditLogRepository.findByModule(module).stream().map(this::mapToResponse).toList();
    }

    @Override
    public List<AuditLogResponseDto> getLogsByEntityId(Long entityId) {
        return auditLogRepository.findByEntityId(entityId).stream().map(this::mapToResponse).toList();
    }

    @Override
    public PagedAuditLogResponseDto getBillingAuditLogs(int page, int size) {

        User loggedInUser = requireAdminAccess();

        Specification<AuditLog> spec = Specification
                .where(AuditLogSpecifications.module(BILLING_MODULE))
                .and(AuditLogSpecifications.visibleTo(loggedInUser));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));

        Page<AuditLog> logPage = auditLogRepository.findAll(spec, pageable);

        return PagedAuditLogResponseDto.builder()
                .logs(logPage.getContent().stream().map(this::mapToResponse).toList())
                .currentPage(logPage.getNumber())
                .totalPages(logPage.getTotalPages())
                .totalElements(logPage.getTotalElements())
                .build();
    }

    @Override
    public AuditDashboardResponseDto getBillingAuditDashboard() {

        User loggedInUser = requireAdminAccess();

        Specification<AuditLog> baseScope = Specification
                .where(AuditLogSpecifications.module(BILLING_MODULE))
                .and(AuditLogSpecifications.visibleTo(loggedInUser));

        long total = auditLogRepository.count(baseScope);

        long created = auditLogRepository.count(
                baseScope.and(AuditLogSpecifications.action("CREATED")));

        long deleted = auditLogRepository.count(
                baseScope.and(AuditLogSpecifications.actionIn(DELETE_ACTIONS)));

        long updated = total - created - deleted;

        return AuditDashboardResponseDto.builder()
                .totalLogs(total)
                .createdActions(created)
                .updatedActions(updated)
                .deletedActions(deleted)
                .build();
    }

    @Override
    public List<AuditLogResponseDto> getBillingActivityTimeline(int limit) {

        User loggedInUser = requireAdminAccess();

        Specification<AuditLog> spec = Specification
                .where(AuditLogSpecifications.module(BILLING_MODULE))
                .and(AuditLogSpecifications.visibleTo(loggedInUser));

        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "timestamp"));

        return auditLogRepository.findAll(spec, pageable).getContent()
                .stream().map(this::mapToResponse).toList();
    }

    private User requireAdminAccess() {

        User loggedInUser = securityUtils.getLoggedInUser();

        if (loggedInUser.getRole() != RoleType.SUPER_ADMIN && loggedInUser.getRole() != RoleType.ADMIN) {
            throw new IllegalArgumentException("You are not allowed to access billing audit logs");
        }

        return loggedInUser;
    }

    private AuditLogResponseDto mapToResponse(AuditLog auditLog) {

        return AuditLogResponseDto.builder()
                .id(auditLog.getId())
                .module(auditLog.getModule())
                .entityId(auditLog.getEntityId())
                .entityType(auditLog.getEntityType())
                .targetAdminId(auditLog.getTargetAdminId())
                .action(auditLog.getAction())
                .performedBy(auditLog.getPerformedBy())
                .description(auditLog.getDescription())
                .timestamp(auditLog.getTimestamp())
                .build();
    }
}
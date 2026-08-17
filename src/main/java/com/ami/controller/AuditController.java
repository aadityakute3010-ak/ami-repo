package com.ami.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.ami.dto.requests.CreateAuditLogRequestDto;
import com.ami.dto.responses.AuditLogResponseDto;
import com.ami.service.AuditService;

@RestController
@RequestMapping("/api/audit-logs")
public class AuditController {

    private final AuditService auditService;

    public AuditController(
            AuditService auditService) {

        this.auditService = auditService;
    }
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping
    public AuditLogResponseDto createAuditLog(
            @RequestBody
            CreateAuditLogRequestDto request) {

        return auditService
                .createAuditLog(request);
    }
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping
    public List<AuditLogResponseDto>
    getAllAuditLogs() {

        return auditService
                .getAllAuditLogs();
    }
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("/module/{module}")
    public List<AuditLogResponseDto>
    getLogsByModule(
            @PathVariable String module) {

        return auditService
                .getLogsByModule(module);
    }
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("/entity/{entityId}")
    public List<AuditLogResponseDto>
    getLogsByEntityId(
            @PathVariable Long entityId) {

        return auditService
                .getLogsByEntityId(entityId);
    }
}
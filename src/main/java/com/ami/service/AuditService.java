package com.ami.service;

import java.util.List;

import com.ami.dto.requests.CreateAuditLogRequestDto;
import com.ami.dto.responses.AuditLogResponseDto;

public interface AuditService {

    AuditLogResponseDto createAuditLog(
            CreateAuditLogRequestDto request);

    List<AuditLogResponseDto> getAllAuditLogs();

    List<AuditLogResponseDto>
    getLogsByModule(
            String module);

    List<AuditLogResponseDto>
    getLogsByEntityId(
            Long entityId);
}
package com.ami.service;

import java.util.List;

import com.ami.dto.requests.CreateAuditLogRequestDto;
import com.ami.dto.responses.AuditDashboardResponseDto;
import com.ami.dto.responses.AuditLogResponseDto;
import com.ami.dto.responses.PagedAuditLogResponseDto;

public interface AuditService {

	AuditLogResponseDto createAuditLog(CreateAuditLogRequestDto request);

	List<AuditLogResponseDto> getAllAuditLogs();

	List<AuditLogResponseDto> getLogsByModule(String module);

	List<AuditLogResponseDto> getLogsByEntityId(Long entityId);

//	List<AuditLogResponseDto> getVisibleBillingAuditLogs();

	PagedAuditLogResponseDto getBillingAuditLogs(int page, int size);

	AuditDashboardResponseDto getBillingAuditDashboard();

	List<AuditLogResponseDto> getBillingActivityTimeline(int limit);
}
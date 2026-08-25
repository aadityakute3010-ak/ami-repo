package com.ami.dto.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuditDashboardResponseDto {

	private long totalLogs;

	private long createdActions;

	private long updatedActions;

	private long deletedActions;
}
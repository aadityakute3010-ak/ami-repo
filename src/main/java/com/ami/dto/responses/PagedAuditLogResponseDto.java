package com.ami.dto.responses;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PagedAuditLogResponseDto {

	private List<AuditLogResponseDto> logs;

	private int currentPage;

	private int totalPages;

	private long totalElements;
}
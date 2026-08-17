package com.ami.dto.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IssueMySummaryResponseDto {

    private Long assigned;

    private Long accepted;

    private Long inProgress;

    private Long resolved;

    private Long rejected;

    private Long escalated;
}
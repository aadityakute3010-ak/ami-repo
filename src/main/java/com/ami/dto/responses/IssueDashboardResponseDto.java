package com.ami.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IssueDashboardResponseDto {

    private Long total;

    private Long open;

    private Long assigned;

    private Long accepted;

    private Long inProgress;

    private Long resolved;

    private Long closed;

    private Long rejected;

    private Long escalated;

    private Long critical;

    private Long overdue;
}
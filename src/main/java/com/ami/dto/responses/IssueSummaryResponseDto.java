package com.ami.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IssueSummaryResponseDto {

    private Long total;

    private Long open;

    private Long accepted;

    private Long inProgress;

    private Long resolved;

    private Long closed;

    private Long escalated;
}
package com.ami.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportSummaryResponseDto {

    private Double totalRevenue;

    private Double totalCollection;

    private Double totalPending;

    private Double totalOverdue;

    private Double totalRecharge;
}
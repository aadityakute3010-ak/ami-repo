package com.ami.dto.responses;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IssueAnalyticsResponseDto {

    // ==========================
    // Overall Statistics
    // ==========================

    private Long totalIssues;

    private Long openIssues;

    private Long inProgressIssues;

    private Long resolvedIssues;

    private Long closedIssues;

    private Long rejectedIssues;

    private Long escalatedIssues;

    private Long overdueIssues;

    // ==========================
    // Resolution Analytics
    // ==========================

    private Double resolutionRate;

    private List<ResolutionTrendResponseDto> monthlyTrend;

    // ==========================
    // Priority Analytics
    // ==========================

    private Long lowPriority;

    private Long mediumPriority;

    private Long highPriority;

    private Long criticalPriority;

    // ==========================
    // Category Analytics
    // ==========================

    private Long meterIssues;

    private Long networkIssues;

    private Long batteryIssues;

    private Long tamperIssues;

    private Long valveIssues;

    private Long communicationIssues;

    private Long billingIssues;

    private Long leakageIssues;

    private Long systemIssues;

    private Long otherIssues;

    // ==========================
    // SLA Analytics
    // ==========================

    private Long slaBreached;

    private Long slaWithin;

    // ==========================
    // Engineer Analytics
    // ==========================

    private Long totalEngineers;

    private Long activeEngineers;

    private Long busyEngineers;

    private Long availableEngineers;
}
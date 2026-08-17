package com.ami.dto.responses;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InstallationAnalyticsResponseDto {

    private Long totalInstallations;

    private Long completedInstallations;

    private Long cancelledInstallations;

    private Long pendingInstallations;

    private Double averageCompletionTime;

    private Double completionRate;

    private Double engineerUtilization;

    private List<MonthlyTrend> monthlyTrend;

    @Data
    @Builder
    public static class MonthlyTrend {

        private String month;

        private Long completed;

        private Long cancelled;

    }

}
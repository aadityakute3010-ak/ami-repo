package com.ami.dto.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AnalyticsResponseDto {

    private Long totalQueries;

    private Long totalUsers;

    private Double averageResponseTime;

    private Long positiveFeedback;

    private Long negativeFeedback;
}
package com.ami.service;

import com.ami.dto.responses.AnalyticsResponseDto;

public interface AnalyticsService {

    AnalyticsResponseDto getAnalytics();

    AnalyticsResponseDto getUsageAnalytics();

    AnalyticsResponseDto getTrendAnalytics();

    AnalyticsResponseDto getModuleAnalytics();
}
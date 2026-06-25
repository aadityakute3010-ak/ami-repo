package com.ami.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ami.dto.responses.AnalyticsResponseDto;
import com.ami.entity.AiAnalytics;
import com.ami.repository.AiAnalyticsRepository;
import com.ami.service.AnalyticsService;

@Service
public class AnalyticsServiceImpl
        implements AnalyticsService {

    private final AiAnalyticsRepository repository;

    public AnalyticsServiceImpl(
            AiAnalyticsRepository repository) {

        this.repository = repository;
    }

    @Override
    public AnalyticsResponseDto getAnalytics() {

        List<AiAnalytics> data =
                repository.findAll();

        return buildResponse(data);
    }

    @Override
    public AnalyticsResponseDto getUsageAnalytics() {

        return buildResponse(
                repository.findAll());
    }

    @Override
    public AnalyticsResponseDto getTrendAnalytics() {

        return buildResponse(
                repository.findAll());
    }

    @Override
    public AnalyticsResponseDto getModuleAnalytics() {

        return buildResponse(
                repository.findAll());
    }

    private AnalyticsResponseDto
    buildResponse(
            List<AiAnalytics> data) {

        long totalQueries =
                data.size();

        long totalUsers =
                data.stream()
                        .map(
                                AiAnalytics::getUserId)
                        .distinct()
                        .count();

        double avgResponseTime =
                data.stream()
                        .mapToLong(
                                AiAnalytics::getResponseTime)
                        .average()
                        .orElse(0);

        long positiveFeedback =
                data.stream()
                        .filter(a ->
                                "POSITIVE".equalsIgnoreCase(
                                        a.getFeedback()))
                        .count();

        long negativeFeedback =
                data.stream()
                        .filter(a ->
                                "NEGATIVE".equalsIgnoreCase(
                                        a.getFeedback()))
                        .count();

        return AnalyticsResponseDto
                .builder()
                .totalQueries(
                        totalQueries)
                .totalUsers(
                        totalUsers)
                .averageResponseTime(
                        avgResponseTime)
                .positiveFeedback(
                        positiveFeedback)
                .negativeFeedback(
                        negativeFeedback)
                .build();
    }
}
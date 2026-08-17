package com.ami.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.ami.dto.responses.AnalyticsResponseDto;
import com.ami.service.AnalyticsService;

@RestController
@RequestMapping("/api/ai/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(
            AnalyticsService analyticsService) {

        this.analyticsService = analyticsService;
    }
    @PreAuthorize(
    	    "hasAnyRole('SUPER_ADMIN','ADMIN')")
    @GetMapping
    public AnalyticsResponseDto
    getAnalytics() {

        return analyticsService
                .getAnalytics();
    }
    @PreAuthorize(
    	    "hasAnyRole('SUPER_ADMIN','ADMIN')")
    @GetMapping("/usage")
    public AnalyticsResponseDto
    getUsageAnalytics() {

        return analyticsService
                .getUsageAnalytics();
    }
    @PreAuthorize(
    	    "hasAnyRole('SUPER_ADMIN','ADMIN')")
    @GetMapping("/trends")
    public AnalyticsResponseDto
    getTrendAnalytics() {

        return analyticsService
                .getTrendAnalytics();
    }
    @PreAuthorize(
    	    "hasAnyRole('SUPER_ADMIN','ADMIN')")
    @GetMapping("/modules")
    public AnalyticsResponseDto
    getModuleAnalytics() {

        return analyticsService
                .getModuleAnalytics();
    }
}
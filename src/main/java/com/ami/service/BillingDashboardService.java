package com.ami.service;

import com.ami.dto.responses.BillingDashboardResponseDto;

public interface BillingDashboardService {

	BillingDashboardResponseDto getDashboard(Integer year);
}
package com.ami.service;

import java.util.List;

import com.ami.dto.requests.CreatePrepaidRechargePlanRequestDto;
import com.ami.dto.requests.UpdatePrepaidRechargePlanRequestDto;
import com.ami.dto.responses.PagedPrepaidRechargePlanResponseDto;
import com.ami.dto.responses.PrepaidRechargePlanResponseDto;
import com.ami.enums.PrepaidPlanStatus;
import com.ami.enums.SourceType;

public interface PrepaidRechargePlanService {

	PrepaidRechargePlanResponseDto createPlan(CreatePrepaidRechargePlanRequestDto request);

	PrepaidRechargePlanResponseDto updatePlan(Long planId, UpdatePrepaidRechargePlanRequestDto request);

	PrepaidRechargePlanResponseDto getPlanById(Long planId);

	PagedPrepaidRechargePlanResponseDto getPlans(int page, int size, String search, SourceType sourceType,
			PrepaidPlanStatus status);

	List<PrepaidRechargePlanResponseDto> getActivePlansForPrepaidDevice(Long deviceId);

	PrepaidRechargePlanResponseDto updatePlanStatus(Long planId, PrepaidPlanStatus status);

	void deletePlan(Long planId);
}
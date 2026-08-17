package com.ami.dto.responses;

import com.ami.enums.EngineerAttendanceStatus;
import com.ami.enums.EngineerAvailabilityStatus;

import lombok.Builder;
import lombok.Data;
@Builder
@Data
public class EngineerDashboardResponseDto {

	private Long assignedIssues;

	private Long inProgressIssues;

	private Long resolvedIssues;

	private Long escalatedIssues;
    
    private Long acceptedIssues;

    private Long completedIssues;

    private Long todayVisits;

    private Long pendingSla;

    private Long resolvedToday;

    private Double resolutionRate;

    private EngineerAttendanceStatus attendance;

    private EngineerAvailabilityStatus availability;

    private Double weeklyPerformance;

    private Double monthlyPerformance;
}
package com.ami.dto.responses;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceTimelineResponseDto {

    private Long id;

    private String deviceId;

    private String title;

    private String assignedEngineer;

    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime assignedAt;

    private LocalDateTime scheduledAt;

    private LocalDateTime startedAt;

    private LocalDateTime completedAt;

    private LocalDateTime cancelledAt;

    private LocalDateTime rescheduledAt;

    private Integer rescheduleCount;

    private Integer assignmentFailureCount;

    private Boolean manualAssignmentRequired;
}
package com.ami.dto.responses;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminAlertAssignmentResponseDto {

    private Long adminId;

    private String adminName;

    private String adminEmail;

    private String assignedBy;

    private LocalDateTime assignedOn;

    private String status;

    private List<String> sources;

    private List<AssignedAlertSummaryDto> alerts;
}
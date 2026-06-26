package com.ami.dto.responses;

import java.time.LocalDateTime;

import com.ami.enums.IssuePriority;
import com.ami.enums.IssueStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IssueResponseDto {

    private Long id;

    private String ticketNumber;

    private String title;

    private String description;

    private IssuePriority priority;

    private IssueStatus status;

    private String customerName;

    private String assignedEngineer;

    private Integer rejectionCount;

    private String comments;

    private String timeline;

    private String attachments;

    private String rejectionHistory;

    private LocalDateTime createdAt;
}
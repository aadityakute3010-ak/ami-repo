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
public class IssueTimelineResponseDto {

    private Long id;

    private String action;

    private String remarks;

    private String performedBy;

    private String performedByRole;

    private LocalDateTime createdAt;
}
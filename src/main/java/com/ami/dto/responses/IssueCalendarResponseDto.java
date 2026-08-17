package com.ami.dto.responses;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IssueCalendarResponseDto {

    private Long id;

    private String ticketNumber;

    private String title;

    private String customerName;

    private LocalDateTime createdAt;

    private String status;
}
package com.ami.dto.responses;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EngineerOperationResponseDto {

    private Long issueId;

    private String ticketNumber;

    private String issueTitle;

    private String engineerName;

    private String priority;

    private String status;

    private String currentLocation;

    private String visitStatus;

    private String slaStatus;

    private LocalDateTime lastUpdated;
}

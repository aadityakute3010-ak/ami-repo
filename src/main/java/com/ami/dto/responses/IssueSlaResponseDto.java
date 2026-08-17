package com.ami.dto.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IssueSlaResponseDto {

    private Long issueId;

    private Integer slaHours;

    private Long elapsedHours;

    private Long remainingHours;

    private Boolean breached;
}

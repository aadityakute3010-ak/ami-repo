package com.ami.dto.responses;

import java.time.LocalDateTime;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecentActivityResponseDto {

    private Long id;

    private String deviceId;

    private String activity;

    private String performedBy;

    private String status;

    private LocalDateTime activityTime;
}
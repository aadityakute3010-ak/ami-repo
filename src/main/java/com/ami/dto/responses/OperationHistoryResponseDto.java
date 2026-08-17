package com.ami.dto.responses;

import java.time.LocalDateTime;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationHistoryResponseDto {

    private Long id;

    private String deviceId;

    private String operationType;

    private String status;

    private String requestedBy;

    private LocalDateTime requestedAt;

    private LocalDateTime completedAt;

    private String response;

    private String remarks;
}
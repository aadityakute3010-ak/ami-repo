package com.ami.dto.responses;

import java.time.LocalDateTime;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationTimelineResponseDto {

    private Long id;

    private String operationType;

    private String action;

    private String performedBy;

    private String status;

    private LocalDateTime createdAt;
}
package com.ami.dto.responses;

import java.time.LocalDateTime;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValveStatusResponseDto {

    private String deviceId;

    private String deviceName;

    private String valveStatus;

    private Boolean open;

    private LocalDateTime lastOpenedAt;

    private LocalDateTime lastClosedAt;

    private String lastOperation;

    private String updatedBy;

    private LocalDateTime updatedAt;
}
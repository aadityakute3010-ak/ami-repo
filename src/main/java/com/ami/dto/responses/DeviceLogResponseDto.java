package com.ami.dto.responses;

import java.time.LocalDateTime;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceLogResponseDto {

    private Long id;

    private String deviceId;

    private String logLevel;

    private String message;

    private String generatedBy;

    private LocalDateTime createdAt;
}
package com.ami.dto.responses;

import java.time.LocalDateTime;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FirmwareResponseDto {

    private String deviceId;

    private String firmwareVersion;

    private String latestVersion;

    private Boolean updateAvailable;

    private String updateStatus;

    private LocalDateTime lastUpdatedAt;
}
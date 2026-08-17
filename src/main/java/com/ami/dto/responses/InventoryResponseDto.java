package com.ami.dto.responses;

import java.time.LocalDateTime;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryResponseDto {

    private String deviceId;

    private String deviceName;

    private String serialNumber;

    private String meterNumber;

    private String imei;

    private String source;

    private String status;

    private String location;

    private String zone;

    private LocalDateTime installationDate;
}
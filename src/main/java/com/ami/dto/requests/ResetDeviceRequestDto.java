package com.ami.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResetDeviceRequestDto {

    private String deviceId;

    private String requestedBy;

    private String reason;
}
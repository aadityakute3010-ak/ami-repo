package com.ami.dto.responses;

import com.ami.enums.BillingType;
import com.ami.enums.DeviceStatus;
import com.ami.enums.SourceType;
import com.ami.enums.TechnologyType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceListResponseDto {

    private Long id;

    private String deviceId;

    private String deviceName;

    private SourceType sourceType;

    private TechnologyType technologyType;

    private String serialNumber;

    private String macAddress;

    private DeviceStatus status;

    private Boolean active;

    private Boolean online;

    private BillingType billingType;

    private String assignedAdmin;

    private String assignedUser;
}
package com.ami.dto.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeviceExportDto {

    private String deviceId;
    private String deviceName;
    private String macAddress;
    private String serialNumber;

    private String meterName;
    private String sourceType;
    private String technologyType;
    private String applicationOfAmi;
    private String amiApplicationType;
    private String diameterSize;
    private Double literPerPulse;
    private Double meterStartReading;

    private String billingType;

    private String customerName;
    private String customerAddress;
    private String buildingOrWing;
    private String area;
    private String zone;
    private String city;
    private String state;
    private String meterLocation;

    private String assignedAdmin;
    private String assignedUser;

    private Boolean active;
    private Boolean online;
}

package com.ami.dto.requests;

import java.time.LocalDateTime;

import com.ami.enums.InstallationPriority;
import com.ami.enums.InstallationSource;

import lombok.Data;

@Data
public class UpdateInstallationRequestDto {

    private String customerId;

    private String customerName;

    private String customerPhone;

    private String customerEmail;

    private String address;

    private String city;

    private String state;

    private String zone;

    private String area;

    private String deviceId;

    private String deviceName;

    private String meterNumber;

    private String serialNumber;

    private InstallationSource source;

    private InstallationPriority priority;

    private LocalDateTime scheduledDate;

    private Double latitude;

    private Double longitude;

   
}
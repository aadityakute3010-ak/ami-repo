package com.ami.dto.requests;

import java.time.LocalDateTime;

import com.ami.enums.InstallationPriority;
import com.ami.enums.InstallationSource;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateInstallationRequestDto {

    @NotBlank(message = "Customer Id is required")
    private String customerId;

    @NotBlank(message = "Customer Name is required")
    private String customerName;

    @NotBlank(message = "Customer Phone is required")
    private String customerPhone;

    private String customerEmail;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    private String state;

    private String zone;

    private String area;

    @NotBlank(message = "Device Id is required")
    private String deviceId;

    @NotBlank(message = "Device Name is required")
    private String deviceName;

    @NotBlank(message = "Meter Number is required")
    private String meterNumber;

    @NotBlank(message = "Serial Number is required")
    private String serialNumber;

    @NotNull(message = "Source is required")
    private InstallationSource source;

    @NotNull(message = "Priority is required")
    private InstallationPriority priority;

    @NotNull(message = "Scheduled Date is required")
    private LocalDateTime scheduledDate;

    private Double latitude;

    private Double longitude;
    
    private String remarks;

    
}
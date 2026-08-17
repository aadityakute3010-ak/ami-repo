package com.ami.dto.requests;

import com.ami.enums.IssueCategory;
import com.ami.enums.IssuePriority;
import com.ami.enums.SourceType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateIssueRequestDto {

    // Basic Information
    private String title;
    private String description;

    private IssueCategory category;
    private IssuePriority priority;
    private SourceType sourceType;

    // Customer Information
    private Long customerId;
    private String customerName;
    private String customerPhone;
    private String customerEmail;

    // Device Information
    private String deviceId;
    private String meterId;
    private String serialNumber;
    private String firmwareVersion;
    private String meterType;

    // Location Information
    private String state;
    private String city;
    private String zone;
    private String area;
    private String address;

    private Double latitude;
    private Double longitude;
}
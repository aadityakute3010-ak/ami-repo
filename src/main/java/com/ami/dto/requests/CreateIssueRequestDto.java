package com.ami.dto.requests;

import com.ami.enums.IssueCategory;
import com.ami.enums.IssuePriority;
import com.ami.enums.SourceType;

import lombok.Data;

@Data
public class CreateIssueRequestDto {

    private String title;
    private String description;

    private IssueCategory category;
    private IssuePriority priority;
    private SourceType sourceType;

    private Long customerId;
    private String customerName;
    private String customerPhone;
    private String customerEmail;

    private String meterId;
    private String meterType;
    private String serialNumber;

    private String state;
    private String city;
    private String address;
}
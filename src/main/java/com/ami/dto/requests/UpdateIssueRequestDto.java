package com.ami.dto.requests;

import com.ami.enums.IssueCategory;
import com.ami.enums.IssuePriority;

import lombok.Data;

@Data
public class UpdateIssueRequestDto {

    private String title;
    private String description;

    private IssueCategory category;
    private IssuePriority priority;

    private String state;
    private String city;
    private String address;
}
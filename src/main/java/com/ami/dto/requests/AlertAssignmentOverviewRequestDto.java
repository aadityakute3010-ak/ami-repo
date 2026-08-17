package com.ami.dto.requests;

import lombok.Data;

@Data
public class AlertAssignmentOverviewRequestDto {

    private int page = 0;

    private int size = 10;

    private String search;

    private String sortBy = "assignedAt";

    private String sortDirection = "DESC";
}
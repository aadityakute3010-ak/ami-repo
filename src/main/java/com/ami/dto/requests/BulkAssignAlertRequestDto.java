package com.ami.dto.requests;

import java.util.List;

import lombok.Data;

@Data
public class BulkAssignAlertRequestDto {

    private List<Long> alertIds;

    private List<Long> adminIds;

    private String reason;
}
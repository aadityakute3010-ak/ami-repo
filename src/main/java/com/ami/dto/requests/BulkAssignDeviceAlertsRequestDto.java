package com.ami.dto.requests;

import java.util.List;

import lombok.Data;

@Data
public class BulkAssignDeviceAlertsRequestDto {

    private List<Long> alertIds;

    private List<String> deviceIds;

    private String reason;
}
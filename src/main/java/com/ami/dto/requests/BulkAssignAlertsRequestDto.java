package com.ami.dto.requests;

import java.util.List;

import lombok.Data;

@Data
public class BulkAssignAlertsRequestDto {

    private List<Long> alertIds;

    private List<Long> adminIds;
}
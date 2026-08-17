package com.ami.dto.requests;

import java.util.List;

import lombok.Data;

@Data
public class AssignAdminAlertsRequestDto {

    private Long alertId;

    private List<Long> adminIds;
}
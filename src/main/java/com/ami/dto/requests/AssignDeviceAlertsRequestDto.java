package com.ami.dto.requests;

import java.util.List;

import lombok.Data;

@Data
public class AssignDeviceAlertsRequestDto {

    private Long deviceId;

    private List<Long> alertIds;
}
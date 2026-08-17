package com.ami.dto.requests;

import lombok.Data;

@Data
public class CreateAlertEventRequestDto {

    private Long alertId;

    private Long deviceId;

    private String message;
}
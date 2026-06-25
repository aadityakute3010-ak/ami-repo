package com.ami.dto.requests;

import lombok.Data;

@Data
public class CommunicationSettingsDto {

    private String wakeupTime;

    private Integer dataSampleCount;
}
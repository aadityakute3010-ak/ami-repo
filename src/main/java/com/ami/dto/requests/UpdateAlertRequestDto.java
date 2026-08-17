package com.ami.dto.requests;

import com.ami.enums.AlertCategory;
import com.ami.enums.AlertSeverity;
import com.ami.enums.AlertSource;
import com.ami.enums.AlertStatus;

import lombok.Data;

@Data
public class UpdateAlertRequestDto {

    private String name;

    private String fieldLabel;

    private String placeholder;

    private Boolean enabled;

    private String value;

    private AlertSeverity severity;

    private AlertSource source;

    private AlertCategory category;

    private String description;

    private String unit;
    
    private AlertStatus status;
    
    private String deviceId;

    private String message;
}
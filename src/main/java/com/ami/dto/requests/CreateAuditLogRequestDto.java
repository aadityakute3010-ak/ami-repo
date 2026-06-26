package com.ami.dto.requests;

import lombok.Data;

@Data
public class CreateAuditLogRequestDto {

    private String module;

    private Long entityId;

    private String action;

    private String performedBy;

    private String description;
}
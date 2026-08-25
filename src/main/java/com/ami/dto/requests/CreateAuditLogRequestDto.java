package com.ami.dto.requests;

import lombok.Data;

@Data
public class CreateAuditLogRequestDto {

    private String module;

    private Long entityId;
    
    private String entityType;
    
    private Long targetAdminId;

    private String action;

    private String performedBy;

    private String description;
}
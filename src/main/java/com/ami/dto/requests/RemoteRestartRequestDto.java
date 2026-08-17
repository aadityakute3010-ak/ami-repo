package com.ami.dto.requests;

import lombok.Data;

@Data
public class RemoteRestartRequestDto {

    private String requestedBy;

    private String remarks;
}
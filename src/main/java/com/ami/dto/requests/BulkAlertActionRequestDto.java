package com.ami.dto.requests;

import java.util.List;

import lombok.Data;

@Data
public class BulkAlertActionRequestDto {

    private List<Long> alertIds;

    private String reason;
}
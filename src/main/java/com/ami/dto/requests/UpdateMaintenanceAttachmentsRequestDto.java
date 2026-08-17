package com.ami.dto.requests;

import java.util.List;

import lombok.Data;

@Data
public class UpdateMaintenanceAttachmentsRequestDto {

    private List<String> attachmentUrls;
}
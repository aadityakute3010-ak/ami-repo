package com.ami.dto.responses;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceAttachmentsResponseDto {

    private Long maintenanceId;

    private List<String> attachmentUrls;

    private Integer attachmentCount;
}
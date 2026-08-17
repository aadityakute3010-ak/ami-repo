package com.ami.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaintenancePhotoResponseDto {

    private Long maintenanceId;

    private String beforePhotoUrl;

    private String afterPhotoUrl;

    private Boolean beforePhotoUploaded;

    private Boolean afterPhotoUploaded;
}
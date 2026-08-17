package com.ami.dto.requests;

import lombok.Data;

@Data
public class UpdateMaintenancePhotoRequestDto {

    private String beforePhotoUrl;

    private String afterPhotoUrl;
}
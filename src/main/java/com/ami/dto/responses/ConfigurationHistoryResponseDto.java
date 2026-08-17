package com.ami.dto.responses;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConfigurationHistoryResponseDto {

    private Long id;

    private Long configurationId;

    private String configurationName;

    private String oldValue;

    private String newValue;

    private String updatedBy;

    private LocalDateTime updatedAt;
}
package com.ami.dto.responses;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VersionHistoryResponseDto {

    private Long id;

    private Long configurationId;

    private String configurationName;

    private String oldValue;

    private String newValue;

    private String updatedBy;

    private LocalDateTime updatedAt;
}
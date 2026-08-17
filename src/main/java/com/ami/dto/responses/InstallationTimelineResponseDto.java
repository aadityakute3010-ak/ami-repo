package com.ami.dto.responses;

import java.time.LocalDateTime;

import com.ami.enums.InstallationTimelineEvent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstallationTimelineResponseDto {

    private Long id;

    private InstallationTimelineEvent event;

    private String description;

    private String performedBy;

    private String performedByRole;

    private String remarks;

    private LocalDateTime eventTime;
}
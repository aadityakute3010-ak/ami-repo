package com.ami.dto.responses;

import java.time.LocalDateTime;

import com.ami.enums.InstallationStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.ami.enums.HistoryStatus;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstallationHistoryResponseDto {

    private Long id;

    private String action;

    private String description;

    private InstallationStatus previousStatus;

    private InstallationStatus newStatus;

    private String status;

    private String performedBy;

    private String performedByRole;

    private String remarks;

    private LocalDateTime createdAt;
}
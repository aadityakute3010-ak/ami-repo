package com.ami.dto.responses;

import java.time.LocalDateTime;

import com.ami.enums.RemarkType;
import com.ami.enums.RemarkVisibility;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstallationRemarkResponseDto {

    private Long id;

    private String remark;

    private RemarkType remarkType;

    private RemarkVisibility visibility;

    private String createdBy;

    private LocalDateTime createdAt;

    private String updatedBy;

    private LocalDateTime updatedAt;
}
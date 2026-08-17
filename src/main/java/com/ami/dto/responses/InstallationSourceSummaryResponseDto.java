package com.ami.dto.responses;

import com.ami.enums.InstallationSource;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InstallationSourceSummaryResponseDto {

    private InstallationSource source;

    private Long total;

    private Long completed;

    private Long pending;

    private Long inProgress;

    private Long cancelled;

}
package com.ami.dto.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EngineerOperationsSummaryResponseDto {

    private Long totalEngineers;

    private Long present;

    private Long absent;

    private Long halfDay;

    private Long onLeave;

    private Long available;

    private Long busy;

    private Long onField;

    private Long offline;
}
package com.ami.dto.responses;

import com.ami.enums.InvoiceStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InvoiceStatusSummaryResponseDto {

    private InvoiceStatus status;

    private long count;
}
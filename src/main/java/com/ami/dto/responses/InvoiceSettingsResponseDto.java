package com.ami.dto.responses;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InvoiceSettingsResponseDto {

    private String invoicePrefix;

    private Integer invoiceDueDays;
    
    private String currency;
}
package com.ami.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceSummaryResponseDto {

    private Long totalInvoices;

    private Long paidInvoices;

    private Long pendingInvoices;

    private Long overdueInvoices;

    private Double totalRevenue;
}
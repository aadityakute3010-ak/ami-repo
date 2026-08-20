package com.ami.dto.responses;

import java.math.BigDecimal;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BillingDashboardResponseDto {

    private BigDecimal totalRevenue;

    private BigDecimal collectedRevenue;

    private BigDecimal pendingRevenue;

    private BigDecimal overdueRevenue;

    private long totalInvoices;

    private long paidInvoices;

    private long pendingInvoices;

    private long overdueInvoices;

    private long failedInvoices;

    private List<RevenueTrendResponseDto> revenueTrend;

    private List<InvoiceStatusSummaryResponseDto> statusSummary;

    private List<SourceWiseRevenueResponseDto> sourceWiseRevenue;
}
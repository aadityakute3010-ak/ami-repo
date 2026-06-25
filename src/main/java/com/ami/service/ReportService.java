package com.ami.service;

import com.ami.dto.responses.ReportSummaryResponseDto;

public interface ReportService {

    ReportSummaryResponseDto getRevenueReport();

    ReportSummaryResponseDto getCollectionReport();

    ReportSummaryResponseDto getPendingReport();

    ReportSummaryResponseDto getOverdueReport();

    ReportSummaryResponseDto getRechargeReport();

    byte[] exportRevenueReport();

    byte[] exportCollectionReport();

    byte[] exportPendingReport();

    byte[] exportOverdueReport();
}
package com.ami.service;

import com.ami.dto.responses.ExportFileResponseDto;

public interface BillingReportExportService {

	ExportFileResponseDto exportBillingReport(String format, Integer year, Integer month);
}
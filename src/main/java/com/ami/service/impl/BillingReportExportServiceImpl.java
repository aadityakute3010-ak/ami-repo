package com.ami.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.ami.dto.responses.BillingDashboardResponseDto;
import com.ami.dto.responses.ExportFileResponseDto;
import com.ami.dto.responses.SourceWiseRevenueResponseDto;
import com.ami.enums.ReportExportFormat;
import com.ami.service.BillingDashboardService;
import com.ami.service.BillingReportExportService;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BillingReportExportServiceImpl implements BillingReportExportService {

	private final BillingDashboardService billingDashboardService;

	@Override
	public ExportFileResponseDto exportBillingReport(String rawFormat, Integer year, Integer month) {

		ReportExportFormat format = ReportExportFormat.fromParam(rawFormat);

		BillingDashboardResponseDto data = billingDashboardService.getDashboard(year, month);

		byte[] content = switch (format) {
		case CSV -> buildCsv(data);
		case EXCEL -> buildExcel(data);
		case PDF -> buildPdf(data);
		};

		return ExportFileResponseDto.builder().file(content).fileName(buildFilename(format, year, month))
				.contentType(resolveContentType(format)).build();
	}

	private String resolveContentType(ReportExportFormat format) {

		return switch (format) {
		case CSV -> "text/csv";
		case EXCEL -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
		case PDF -> "application/pdf";
		};
	}

	private String buildFilename(ReportExportFormat format, Integer year, Integer month) {

		String extension = switch (format) {
		case CSV -> "csv";
		case EXCEL -> "xlsx";
		case PDF -> "pdf";
		};

		String scope;

		if (year != null && month != null) {
			scope = String.format("%04d-%02d", year, month);
		} else if (year != null) {
			scope = String.valueOf(year);
		} else if (month != null) {
			scope = java.time.Month.of(month).name().toLowerCase();
		} else {
			scope = "all-time";
		}

		return "billing-report-" + scope + "." + extension;
	}

	// ---------- CSV ----------

	private byte[] buildCsv(BillingDashboardResponseDto data) {

		StringBuilder csv = new StringBuilder();

		csv.append("Billing Report\n\n");

		csv.append("Summary\n");
		csv.append("Metric,Value\n");
		appendCsvRow(csv, "Total Revenue", money(data.getTotalRevenue()));
		appendCsvRow(csv, "Collected Revenue", money(data.getCollectedRevenue()));
		appendCsvRow(csv, "Pending Revenue", money(data.getPendingRevenue()));
		appendCsvRow(csv, "Overdue Revenue", money(data.getOverdueRevenue()));
		appendCsvRow(csv, "Total Invoices", String.valueOf(data.getTotalInvoices()));
		appendCsvRow(csv, "Paid Invoices", String.valueOf(data.getPaidInvoices()));
		appendCsvRow(csv, "Pending Invoices", String.valueOf(data.getPendingInvoices()));
		appendCsvRow(csv, "Overdue Invoices", String.valueOf(data.getOverdueInvoices()));
		appendCsvRow(csv, "Failed Invoices", String.valueOf(data.getFailedInvoices()));

		csv.append("\nRevenue Collection Report (Source-wise)\n");
		csv.append("Source,Invoices,Revenue,Collected,Pending,Overdue,Collection %\n");

		for (SourceWiseRevenueResponseDto row : data.getSourceWiseRevenue()) {
			csv.append(row.getSource()).append(',').append(row.getInvoices()).append(',')
					.append(money(row.getRevenue())).append(',').append(money(row.getCollected())).append(',')
					.append(money(row.getPending())).append(',').append(money(row.getOverdue())).append(',')
					.append(money(row.getCollectionPercentage())).append('\n');
		}

		return csv.toString().getBytes(StandardCharsets.UTF_8);
	}

	private void appendCsvRow(StringBuilder csv, String label, String value) {
		csv.append(label).append(',').append(value).append('\n');
	}

	// ---------- Excel ----------

	private byte[] buildExcel(BillingDashboardResponseDto data) {

		try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

			XSSFSheet sheet = workbook.createSheet("Billing Report");

			CellStyle headerStyle = workbook.createCellStyle();
			Font headerFont = workbook.createFont();
			headerFont.setBold(true);
			headerFont.setColor(IndexedColors.WHITE.getIndex());
			headerStyle.setFont(headerFont);
			headerStyle.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
			headerStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);

			int rowIdx = 0;

			rowIdx = writeExcelSectionTitle(sheet, rowIdx, "Summary");
			rowIdx = writeExcelHeaderRow(sheet, rowIdx, headerStyle, "Metric", "Value");
			rowIdx = writeExcelKeyValueRow(sheet, rowIdx, "Total Revenue", money(data.getTotalRevenue()));
			rowIdx = writeExcelKeyValueRow(sheet, rowIdx, "Collected Revenue", money(data.getCollectedRevenue()));
			rowIdx = writeExcelKeyValueRow(sheet, rowIdx, "Pending Revenue", money(data.getPendingRevenue()));
			rowIdx = writeExcelKeyValueRow(sheet, rowIdx, "Overdue Revenue", money(data.getOverdueRevenue()));
			rowIdx = writeExcelKeyValueRow(sheet, rowIdx, "Total Invoices", String.valueOf(data.getTotalInvoices()));
			rowIdx = writeExcelKeyValueRow(sheet, rowIdx, "Paid Invoices", String.valueOf(data.getPaidInvoices()));
			rowIdx = writeExcelKeyValueRow(sheet, rowIdx, "Pending Invoices",
					String.valueOf(data.getPendingInvoices()));
			rowIdx = writeExcelKeyValueRow(sheet, rowIdx, "Overdue Invoices",
					String.valueOf(data.getOverdueInvoices()));
			rowIdx = writeExcelKeyValueRow(sheet, rowIdx, "Failed Invoices", String.valueOf(data.getFailedInvoices()));

			rowIdx++;

			rowIdx = writeExcelSectionTitle(sheet, rowIdx, "Revenue Collection Report (Source-wise)");
			rowIdx = writeExcelHeaderRow(sheet, rowIdx, headerStyle, "Source", "Invoices", "Revenue", "Collected",
					"Pending", "Overdue", "Collection %");

			for (SourceWiseRevenueResponseDto sourceRow : data.getSourceWiseRevenue()) {

				Row row = sheet.createRow(rowIdx++);
				row.createCell(0).setCellValue(sourceRow.getSource().name());
				row.createCell(1).setCellValue(sourceRow.getInvoices());
				row.createCell(2).setCellValue(money(sourceRow.getRevenue()));
				row.createCell(3).setCellValue(money(sourceRow.getCollected()));
				row.createCell(4).setCellValue(money(sourceRow.getPending()));
				row.createCell(5).setCellValue(money(sourceRow.getOverdue()));
				row.createCell(6).setCellValue(money(sourceRow.getCollectionPercentage()));
			}

			for (int col = 0; col <= 6; col++) {
				sheet.autoSizeColumn(col);
			}

			workbook.write(out);

			return out.toByteArray();

		} catch (IOException ex) {
			throw new UncheckedIOException("Failed to generate Excel billing report", ex);
		}
	}

	private int writeExcelSectionTitle(XSSFSheet sheet, int rowIdx, String title) {

		Row row = sheet.createRow(rowIdx);
		Cell cell = row.createCell(0);
		cell.setCellValue(title);

		return rowIdx + 1;
	}

	private int writeExcelHeaderRow(XSSFSheet sheet, int rowIdx, CellStyle headerStyle, String... headers) {

		Row row = sheet.createRow(rowIdx);

		for (int i = 0; i < headers.length; i++) {
			Cell cell = row.createCell(i);
			cell.setCellValue(headers[i]);
			cell.setCellStyle(headerStyle);
		}

		return rowIdx + 1;
	}

	private int writeExcelKeyValueRow(XSSFSheet sheet, int rowIdx, String label, String value) {

		Row row = sheet.createRow(rowIdx);
		row.createCell(0).setCellValue(label);
		row.createCell(1).setCellValue(value);

		return rowIdx + 1;
	}

	// ---------- PDF ----------

	private byte[] buildPdf(BillingDashboardResponseDto data) {

		Document document = new Document(PageSize.A4);

		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

			PdfWriter.getInstance(document, out);
			document.open();

			com.lowagie.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
			com.lowagie.text.Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13);
			com.lowagie.text.Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10,
					java.awt.Color.WHITE);
			com.lowagie.text.Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

			document.add(new Paragraph("Billing Report", titleFont));
			document.add(new Paragraph(" "));

			document.add(new Paragraph("Summary", sectionFont));
			document.add(new Paragraph(" "));

			PdfPTable summaryTable = new PdfPTable(2);
			summaryTable.setWidthPercentage(100);
			addPdfSummaryRow(summaryTable, "Total Revenue", money(data.getTotalRevenue()), cellFont);
			addPdfSummaryRow(summaryTable, "Collected Revenue", money(data.getCollectedRevenue()), cellFont);
			addPdfSummaryRow(summaryTable, "Pending Revenue", money(data.getPendingRevenue()), cellFont);
			addPdfSummaryRow(summaryTable, "Overdue Revenue", money(data.getOverdueRevenue()), cellFont);
			addPdfSummaryRow(summaryTable, "Total Invoices", String.valueOf(data.getTotalInvoices()), cellFont);
			addPdfSummaryRow(summaryTable, "Paid Invoices", String.valueOf(data.getPaidInvoices()), cellFont);
			addPdfSummaryRow(summaryTable, "Pending Invoices", String.valueOf(data.getPendingInvoices()), cellFont);
			addPdfSummaryRow(summaryTable, "Overdue Invoices", String.valueOf(data.getOverdueInvoices()), cellFont);
			addPdfSummaryRow(summaryTable, "Failed Invoices", String.valueOf(data.getFailedInvoices()), cellFont);
			document.add(summaryTable);

			document.add(new Paragraph(" "));
			document.add(new Paragraph("Revenue Collection Report (Source-wise)", sectionFont));
			document.add(new Paragraph(" "));

			PdfPTable sourceTable = new PdfPTable(7);
			sourceTable.setWidthPercentage(100);

			for (String header : new String[] { "Source", "Invoices", "Revenue", "Collected", "Pending", "Overdue",
					"Collection %" }) {
				PdfPCell headerCell = new PdfPCell(new Phrase(header, headerFont));
				headerCell.setBackgroundColor(new java.awt.Color(80, 80, 80));
				headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				sourceTable.addCell(headerCell);
			}

			for (SourceWiseRevenueResponseDto row : data.getSourceWiseRevenue()) {
				sourceTable.addCell(new Phrase(row.getSource().name(), cellFont));
				sourceTable.addCell(new Phrase(String.valueOf(row.getInvoices()), cellFont));
				sourceTable.addCell(new Phrase(money(row.getRevenue()), cellFont));
				sourceTable.addCell(new Phrase(money(row.getCollected()), cellFont));
				sourceTable.addCell(new Phrase(money(row.getPending()), cellFont));
				sourceTable.addCell(new Phrase(money(row.getOverdue()), cellFont));
				sourceTable.addCell(new Phrase(money(row.getCollectionPercentage()), cellFont));
			}

			document.add(sourceTable);
			document.close();

			return out.toByteArray();

		} catch (DocumentException | IOException ex) {
			throw new IllegalStateException("Failed to generate PDF billing report", ex);
		} finally {
			if (document.isOpen()) {
				document.close();
			}
		}
	}

	private void addPdfSummaryRow(PdfPTable table, String label, String value, com.lowagie.text.Font font) {
		table.addCell(new Phrase(label, font));
		table.addCell(new Phrase(value, font));
	}

	// ---------- shared ----------

	private String money(BigDecimal value) {
		return (value == null ? BigDecimal.ZERO : value).setScale(2, java.math.RoundingMode.HALF_UP).toPlainString();
	}
}
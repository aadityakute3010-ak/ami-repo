package com.ami.service.impl;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;

import com.ami.entity.Invoice;
import com.ami.service.InvoicePdfService;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

@Service
public class InvoicePdfServiceImpl implements InvoicePdfService {

	@Override
	public byte[] generateInvoicePdf(Invoice invoice) {

		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

			Document document = new Document(PageSize.A4);
			PdfWriter.getInstance(document, outputStream);

			document.open();

			Font titleFont = new Font(Font.HELVETICA, 20, Font.BOLD);
			Font sectionFont = new Font(Font.HELVETICA, 12, Font.BOLD);
			Font normalFont = new Font(Font.HELVETICA, 10);

			Paragraph title = new Paragraph("AMI BILLING INVOICE", titleFont);
			title.setAlignment(Element.ALIGN_CENTER);
			document.add(title);

			document.add(new Paragraph(" "));

			document.add(new Paragraph("Invoice Number: " + safe(invoice.getInvoiceNumber()), normalFont));
			document.add(new Paragraph("Invoice Date: " + safe(invoice.getInvoiceDate()), normalFont));
			document.add(new Paragraph("Due Date: " + safe(invoice.getDueDate()), normalFont));
			document.add(new Paragraph("Billing Period: " + safe(invoice.getBillingPeriodFrom()) + " to "
					+ safe(invoice.getBillingPeriodTo()), normalFont));

			document.add(new Paragraph(" "));

			document.add(new Paragraph("Customer Details", sectionFont));
			document.add(new Paragraph("Customer Name: " + safe(invoice.getCustomerName()), normalFont));
			document.add(new Paragraph("Email: " + safe(invoice.getEmail()), normalFont));
			document.add(new Paragraph("Phone: " + safe(invoice.getPhone()), normalFont));

			document.add(new Paragraph(" "));

			document.add(new Paragraph("Device / Meter Details", sectionFont));
			document.add(new Paragraph("Meter Number: " + safe(invoice.getMeterNumber()), normalFont));
			document.add(new Paragraph("Source: " + safe(invoice.getSource()), normalFont));
			document.add(new Paragraph("Billing Type: " + safe(invoice.getBillingType()), normalFont));
			document.add(new Paragraph(
					"Tariff: " + (invoice.getTariff() != null ? safe(invoice.getTariff().getName()) : "-"),
					normalFont));

			document.add(new Paragraph(" "));

			document.add(new Paragraph("Reading Details", sectionFont));

			PdfPTable readingTable = new PdfPTable(2);
			readingTable.setWidthPercentage(100);
			readingTable.addCell("Previous Reading");
			readingTable.addCell(format(invoice.getPreviousReading()));
			readingTable.addCell("Current Reading");
			readingTable.addCell(format(invoice.getCurrentReading()));
			readingTable.addCell("Consumption");
			readingTable.addCell(format(invoice.getConsumption()));
			document.add(readingTable);

			document.add(new Paragraph(" "));

			document.add(new Paragraph("Amount Details", sectionFont));

			PdfPTable amountTable = new PdfPTable(2);
			amountTable.setWidthPercentage(100);
			amountTable.addCell("Consumption Amount");
			amountTable.addCell(format(invoice.getAmount()));
			amountTable.addCell("Fixed Charge");
			amountTable.addCell(format(invoice.getFixedCharge()));
			amountTable.addCell("Tax");
			document.add(new Paragraph("Previous Dues: ₹" + formatAmount(invoice.getPreviousDues())));
			amountTable.addCell(format(invoice.getTax()));
			amountTable.addCell("Discount");
			amountTable.addCell(format(invoice.getDiscount()));
			amountTable.addCell("Penalty Amount");
			amountTable.addCell(format(invoice.getPenaltyAmount()));
			amountTable.addCell("Net Amount");
			amountTable.addCell(format(invoice.getNetAmount()));
			amountTable.addCell("Paid Amount");
			amountTable.addCell(format(invoice.getPaidAmount()));
			amountTable.addCell("Balance Amount");
			amountTable.addCell(format(invoice.getBalanceAmount()));
			document.add(amountTable);

			document.add(new Paragraph(" "));

			document.add(new Paragraph("Invoice Status: " + safe(invoice.getStatus()), normalFont));
			document.add(new Paragraph("Payment Status: " + safe(invoice.getPaymentStatus()), normalFont));

			if (invoice.getRemarks() != null && !invoice.getRemarks().isBlank()) {
				document.add(new Paragraph("Remarks: " + invoice.getRemarks(), normalFont));
			}

			document.add(new Paragraph(" "));
			Paragraph footer = new Paragraph("This is a system generated invoice.", normalFont);
			footer.setAlignment(Element.ALIGN_CENTER);
			document.add(footer);

			document.close();

			return outputStream.toByteArray();

		} catch (Exception e) {
			throw new RuntimeException("Failed to generate invoice PDF", e);
		}
	}

	private String formatAmount(BigDecimal amount) {
		return amount == null ? "0.00" : amount.setScale(2, RoundingMode.HALF_UP).toString();
	}

	private String safe(Object value) {
		return value == null ? "-" : value.toString();
	}

	private String format(BigDecimal value) {
		return value == null ? "0.00" : value.setScale(2, RoundingMode.HALF_UP).toString();
	}
}
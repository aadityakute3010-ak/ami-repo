package com.ami.service.impl;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import com.ami.entity.Invoice;
import com.ami.entity.Payment;
import com.ami.service.PaymentReceiptPdfService;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

@Service
public class PaymentReceiptPdfServiceImpl implements PaymentReceiptPdfService {

	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

	@Override
	public byte[] generatePaymentReceiptPdf(Payment payment) {

		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

			Document document = new Document();
			PdfWriter.getInstance(document, outputStream);

			document.open();

			addHeader(document, payment);
			addCustomerAndInvoiceDetails(document, payment);
			addPaymentDetails(document, payment);
			addAmountSummary(document, payment);
			addFooter(document);

			document.close();

			return outputStream.toByteArray();

		} catch (Exception exception) {
			throw new IllegalStateException("Unable to generate payment receipt PDF", exception);
		}
	}

	private void addHeader(Document document, Payment payment) throws Exception {

		Font titleFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD);
		Font subTitleFont = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL);

		Paragraph title = new Paragraph("PAYMENT RECEIPT", titleFont);
		title.setAlignment(Element.ALIGN_CENTER);
		document.add(title);

		Paragraph receiptNo = new Paragraph("Receipt No: " + buildReceiptNumber(payment), subTitleFont);
		receiptNo.setAlignment(Element.ALIGN_CENTER);
		document.add(receiptNo);

		Paragraph status = new Paragraph("Payment Status: " + payment.getStatus(), subTitleFont);
		status.setAlignment(Element.ALIGN_CENTER);
		document.add(status);

		document.add(Chunk.NEWLINE);
	}

	private void addCustomerAndInvoiceDetails(Document document, Payment payment) throws Exception {

		Invoice invoice = payment.getInvoice();

		PdfPTable table = new PdfPTable(2);
		table.setWidthPercentage(100);
		table.setWidths(new float[] { 1, 1 });

		PdfPCell customerCell = createSectionCell("Customer Details");
		customerCell.addElement(new Paragraph("Name: " + nullSafe(payment.getCustomerName())));
		customerCell.addElement(new Paragraph("Email: " + nullSafe(invoice != null ? invoice.getEmail() : null)));
		customerCell.addElement(new Paragraph("Phone: " + nullSafe(invoice != null ? invoice.getPhone() : null)));

		PdfPCell invoiceCell = createSectionCell("Invoice Details");
		invoiceCell.addElement(
				new Paragraph("Invoice No: " + nullSafe(invoice != null ? invoice.getInvoiceNumber() : null)));
		invoiceCell.addElement(new Paragraph("Meter No: " + nullSafe(payment.getMeterNumber())));
		invoiceCell.addElement(new Paragraph("Source: " + nullSafe(payment.getSource())));
		invoiceCell.addElement(new Paragraph("Billing Period: " + nullSafe(payment.getBillingPeriod())));
		invoiceCell.addElement(new Paragraph("Due Date: " + nullSafe(payment.getDueDate())));

		table.addCell(customerCell);
		table.addCell(invoiceCell);

		document.add(table);
		document.add(Chunk.NEWLINE);
	}

	private void addPaymentDetails(Document document, Payment payment) throws Exception {

		PdfPTable table = new PdfPTable(2);
		table.setWidthPercentage(100);
		table.setWidths(new float[] { 1, 2 });

		addTitleRow(table, "Payment Details");

		addRow(table, "Transaction ID", payment.getTransactionId());
		addRow(table, "Payment Date",
				payment.getPaymentDate() != null ? payment.getPaymentDate().format(DATE_TIME_FORMATTER) : "-");
		addRow(table, "Payment Method", payment.getMethod());
		addRow(table, "Payment Gateway", payment.getGateway());
		addRow(table, "Reference Number", payment.getReferenceNumber());
		addRow(table, "Razorpay Order ID", payment.getRazorpayOrderId());
		addRow(table, "Razorpay Payment ID", payment.getRazorpayPaymentId());
		addRow(table, "Remarks", payment.getRemarks());

		document.add(table);
		document.add(Chunk.NEWLINE);
	}

	private void addAmountSummary(Document document, Payment payment) throws Exception {

		Invoice invoice = payment.getInvoice();

		PdfPTable table = new PdfPTable(2);
		table.setWidthPercentage(60);
		table.setHorizontalAlignment(Element.ALIGN_RIGHT);
		table.setWidths(new float[] { 2, 1 });

		addTitleRow(table, "Amount Summary");

		addAmountRow(table, "Amount Paid", payment.getAmount());
		addAmountRow(table, "Invoice Net Amount", invoice != null ? invoice.getNetAmount() : null);
		addAmountRow(table, "Total Paid", invoice != null ? invoice.getPaidAmount() : null);
		addAmountRow(table, "Balance Amount", invoice != null ? invoice.getBalanceAmount() : null);

		document.add(table);
	}

	private void addFooter(Document document) throws Exception {

		document.add(Chunk.NEWLINE);
		document.add(Chunk.NEWLINE);

		Font footerFont = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC);

		Paragraph footer = new Paragraph("This is a system generated receipt. No signature is required.", footerFont);
		footer.setAlignment(Element.ALIGN_CENTER);

		document.add(footer);
	}

	private PdfPCell createSectionCell(String title) {

		PdfPCell cell = new PdfPCell();
		cell.setPadding(10);

		Font headingFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);

		Paragraph heading = new Paragraph(title, headingFont);
		heading.setSpacingAfter(8);

		cell.addElement(heading);

		return cell;
	}

	private void addTitleRow(PdfPTable table, String title) {

		Font font = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);

		PdfPCell cell = new PdfPCell(new Phrase(title, font));
		cell.setColspan(2);
		cell.setPadding(8);
		cell.setBackgroundColor(BaseColor.DARK_GRAY);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);

		table.addCell(cell);
	}

	private void addRow(PdfPTable table, String label, Object value) {

		table.addCell(createCell(label, true));
		table.addCell(createCell(nullSafe(value), false));
	}

	private void addAmountRow(PdfPTable table, String label, BigDecimal amount) {

		table.addCell(createCell(label, true));
		table.addCell(createCell("₹ " + formatAmount(amount), false));
	}

	private PdfPCell createCell(String value, boolean bold) {

		Font font = new Font(Font.FontFamily.HELVETICA, 10, bold ? Font.BOLD : Font.NORMAL);

		PdfPCell cell = new PdfPCell(new Phrase(value, font));
		cell.setPadding(7);
		cell.setBorder(Rectangle.BOX);

		return cell;
	}

	private String buildReceiptNumber(Payment payment) {

		if (payment.getTransactionId() == null) {
			return "RCPT-" + payment.getId();
		}

		return "RCPT-" + payment.getTransactionId();
	}

	private String formatAmount(BigDecimal amount) {

		return amount == null ? "0.00" : amount.setScale(2, RoundingMode.HALF_UP).toString();
	}

	private String nullSafe(Object value) {

		return value == null ? "-" : value.toString();
	}
}
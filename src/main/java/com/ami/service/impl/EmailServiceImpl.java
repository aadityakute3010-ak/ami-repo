package com.ami.service.impl;

import java.math.BigDecimal;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import com.ami.entity.Invoice;
import com.ami.service.EmailService;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import com.ami.entity.Payment;
import org.springframework.core.io.ByteArrayResource;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

	private final JavaMailSender mailSender;

	@Override
	public void sendResetPasswordEmail(String toEmail, String firstName, String resetLink) {

		String subject = "AMI System - Password Reset Request";
		String body = "Hello " + firstName + ",\n\n"
				+ "We received a request to reset your password for your AMI account.\n\n"
				+ "Click the link below to reset your password:\n\n" + resetLink + "\n\n"
				+ "This link will expire in 30 minutes.\n\n"
				+ "If you did not request a password reset, please ignore this email.\n\n" + "Regards,\n"
				+ "AMI Support Team";

		SimpleMailMessage message = new SimpleMailMessage();

		message.setTo(toEmail);

		message.setSubject(subject);

		message.setText(body);

		mailSender.send(message);
	}

	@Override
	public void sendInvoiceGeneratedEmail(Invoice invoice, byte[] pdfBytes) {

		if (invoice == null || invoice.getEmail() == null || invoice.getEmail().isBlank()) {
			return;
		}

		try {
			MimeMessage message = mailSender.createMimeMessage();

			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

			helper.setTo(invoice.getEmail());
			helper.setSubject("AMI Invoice Generated | " + invoice.getInvoiceNumber());

			String body = buildInvoiceEmailBody(invoice);

			helper.setText(body, true);

			helper.addAttachment(invoice.getInvoiceNumber() + ".pdf", () -> new java.io.ByteArrayInputStream(pdfBytes));

			mailSender.send(message);

		} catch (Exception e) {
			throw new RuntimeException("Failed to send invoice email", e);
		}
	}

	private String buildInvoiceEmailBody(Invoice invoice) {

		return """
				<!DOCTYPE html>
				<html>
				<body style="font-family: Arial, sans-serif; background-color: #f4f6f8; padding: 24px;">
				    <div style="max-width: 650px; margin: auto; background: #ffffff; border-radius: 8px; overflow: hidden; border: 1px solid #e5e7eb;">

				        <div style="background-color: #0f172a; color: #ffffff; padding: 20px 28px;">
				            <h2 style="margin: 0;">AMI Billing Invoice</h2>
				            <p style="margin: 6px 0 0;">Your monthly utility invoice has been generated.</p>
				        </div>

				        <div style="padding: 28px;">
				            <p style="font-size: 15px;">Dear <strong>%s</strong>,</p>

				            <p style="font-size: 14px; color: #374151;">
				                Your invoice for the billing period <strong>%s to %s</strong> has been generated successfully.
				                Please find the invoice PDF attached with this email.
				            </p>

				            <table style="width: 100%%; border-collapse: collapse; margin-top: 20px;">
				                <tr>
				                    <td style="padding: 10px; border: 1px solid #e5e7eb;">Invoice Number</td>
				                    <td style="padding: 10px; border: 1px solid #e5e7eb;"><strong>%s</strong></td>
				                </tr>
				                <tr>
				                    <td style="padding: 10px; border: 1px solid #e5e7eb;">Meter Number</td>
				                    <td style="padding: 10px; border: 1px solid #e5e7eb;">%s</td>
				                </tr>
				                <tr>
				                    <td style="padding: 10px; border: 1px solid #e5e7eb;">Source</td>
				                    <td style="padding: 10px; border: 1px solid #e5e7eb;">%s</td>
				                </tr>
				                <tr>
				                    <td style="padding: 10px; border: 1px solid #e5e7eb;">Consumption</td>
				                    <td style="padding: 10px; border: 1px solid #e5e7eb;">%s</td>
				                </tr>
				                <tr>
				                    <td style="padding: 10px; border: 1px solid #e5e7eb;">Due Date</td>
				                    <td style="padding: 10px; border: 1px solid #e5e7eb;">%s</td>
				                </tr>
				                <tr>
				                    <td style="padding: 10px; border: 1px solid #e5e7eb;">Total Payable Amount</td>
				                    <td style="padding: 10px; border: 1px solid #e5e7eb; font-size: 18px;">
				                        <strong>₹ %s</strong>
				                    </td>
				                </tr>
				            </table>

				            <p style="font-size: 13px; color: #6b7280; margin-top: 24px;">
				                This is a system generated email. Please do not reply to this message.
				            </p>

				            <p style="font-size: 14px; margin-top: 24px;">
				                Regards,<br>
				                <strong>AMI Billing Team</strong>
				            </p>
				        </div>
				    </div>
				</body>
				</html>
				"""
				.formatted(safe(invoice.getCustomerName()), safe(invoice.getBillingPeriodFrom()),
						safe(invoice.getBillingPeriodTo()), safe(invoice.getInvoiceNumber()),
						safe(invoice.getMeterNumber()), safe(invoice.getSource()), safe(invoice.getConsumption()),
						safe(invoice.getDueDate()), format(invoice.getNetAmount()));
	}

	private String safe(Object value) {
		return value == null ? "-" : value.toString();
	}

	private String format(BigDecimal value) {
		return value == null ? "0.00" : value.setScale(2, java.math.RoundingMode.HALF_UP).toString();
	}

	@Override
	public void sendPaymentReceiptEmail(Payment payment, byte[] receiptPdfBytes) {

		try {
			Invoice invoice = payment.getInvoice();

			if (invoice == null || invoice.getEmail() == null || invoice.getEmail().isBlank()) {
				throw new IllegalStateException("Customer email is not available for payment receipt");
			}

			MimeMessage message = mailSender.createMimeMessage();

			MimeMessageHelper helper = new MimeMessageHelper(message, true);

			helper.setTo(invoice.getEmail());
			helper.setSubject("Payment Receipt - " + payment.getTransactionId());

			String body = """
					Dear %s,

					We have received your payment successfully.

					Payment Details:
					Transaction ID: %s
					Invoice Number: %s
					Amount Paid: ₹%s
					Payment Method: %s
					Payment Date: %s

					Please find your payment receipt attached.

					Thank you,
					AMI Billing Team
					""".formatted(payment.getCustomerName() != null ? payment.getCustomerName() : "Customer",
					payment.getTransactionId(), invoice.getInvoiceNumber(), payment.getAmount(), payment.getMethod(),
					payment.getPaymentDate());

			helper.setText(body);

			helper.addAttachment("Payment-Receipt-" + payment.getTransactionId() + ".pdf",
					new ByteArrayResource(receiptPdfBytes));

			mailSender.send(message);

		} catch (Exception exception) {
			throw new IllegalStateException("Unable to send payment receipt email: " + exception.getMessage(),
					exception);
		}
	}

}
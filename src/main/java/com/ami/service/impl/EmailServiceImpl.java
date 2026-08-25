package com.ami.service.impl;

import java.math.BigDecimal;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import com.ami.entity.Invoice;
import com.ami.service.EmailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import com.ami.entity.Payment;
import org.springframework.core.io.ByteArrayResource;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

	private final JavaMailSender mailSender;

	@Value("${spring.mail.username}")
	private String fromEmail;

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

	@Override
	public void sendRechargeSuccessEmail(String recipientEmail, String customerName, String deviceIdentifier,
			String rechargeNumber, BigDecimal amount, BigDecimal creditedUnits, BigDecimal balanceBefore,
			BigDecimal balanceAfter, String paymentId, String paymentMethod, String currency,
			LocalDateTime rechargeDate) {

		try {
			byte[] pdfReceipt = generateRechargeReceiptPdf(customerName, deviceIdentifier, rechargeNumber, amount,
					creditedUnits, balanceBefore, balanceAfter, paymentId, paymentMethod, currency, rechargeDate);

			MimeMessage message = mailSender.createMimeMessage();

			MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

			helper.setFrom(fromEmail);
			helper.setTo(recipientEmail);
			helper.setSubject("Prepaid Recharge Successful | " + rechargeNumber);

			String body = buildRechargeSuccessEmailBody(customerName, deviceIdentifier, rechargeNumber, amount,
					creditedUnits, balanceAfter, paymentId, currency, rechargeDate);

			helper.setText(body, false);

			helper.addAttachment("AMI-Recharge-Receipt-" + rechargeNumber + ".pdf", new ByteArrayResource(pdfReceipt));

			mailSender.send(message);

			log.info("Recharge success email sent successfully to {} for recharge {}", recipientEmail, rechargeNumber);

		} catch (Exception e) {

			log.error("Failed to send recharge success email for recharge {}", rechargeNumber, e);

			throw new RuntimeException("Unable to send recharge success email", e);
		}
	}

	// ============================================================
	// FAILURE EMAIL
	// ============================================================

	@Override
	public void sendRechargeFailureEmail(String recipientEmail, String customerName, String deviceIdentifier,
			String rechargeNumber, BigDecimal amount, String currency, String failureReason,
			LocalDateTime rechargeDate) {

		try {

			MimeMessage message = mailSender.createMimeMessage();

			MimeMessageHelper helper = new MimeMessageHelper(message, false, StandardCharsets.UTF_8.name());

			helper.setFrom(fromEmail);
			helper.setTo(recipientEmail);

			helper.setSubject("Prepaid Recharge Failed | " + rechargeNumber);

			String body = buildRechargeFailureEmailBody(customerName, deviceIdentifier, rechargeNumber, amount,
					currency, failureReason, rechargeDate);

			helper.setText(body, false);

			mailSender.send(message);

			log.info("Recharge failure email sent successfully to {} for recharge {}", recipientEmail, rechargeNumber);

		} catch (Exception e) {

			log.error("Failed to send recharge failure email for recharge {}", rechargeNumber, e);

			throw new RuntimeException("Unable to send recharge failure email", e);
		}
	}

	// ============================================================
	// SUCCESS EMAIL BODY
	// ============================================================

	private String buildRechargeSuccessEmailBody(String customerName, String deviceIdentifier, String rechargeNumber,
			BigDecimal amount, BigDecimal creditedUnits, BigDecimal balanceAfter, String paymentId, String currency,
			LocalDateTime rechargeDate) {

		return """
				Dear %s,

				Your prepaid recharge has been completed successfully.

				We have received and verified your payment, and the
				corresponding prepaid units have been credited to your device.

				============================================================
				RECHARGE DETAILS
				============================================================

				Recharge Number : %s
				Device          : %s
				Payment ID      : %s
				Recharge Date   : %s

				Amount Paid     : %s %s
				Units Credited  : %s
				New Balance     : %s

				============================================================

				Your updated prepaid balance is now available for use.

				A professional PDF receipt for this transaction is attached
				to this email for your records.

				If you did not authorize this transaction or believe any
				information is incorrect, please contact our support team.

				Thank you for choosing AMI.

				Regards,
				AMI Support Team
				""".formatted(customerName, rechargeNumber, deviceIdentifier, paymentId, rechargeDate, currency, amount,
				creditedUnits, balanceAfter);
	}

	// ============================================================
	// FAILURE EMAIL BODY
	// ============================================================

	private String buildRechargeFailureEmailBody(String customerName, String deviceIdentifier, String rechargeNumber,
			BigDecimal amount, String currency, String failureReason, LocalDateTime rechargeDate) {

		return """
				Dear %s,

				We were unable to complete your prepaid recharge.

				No prepaid units have been credited to your device as part
				of this unsuccessful transaction.

				============================================================
				RECHARGE DETAILS
				============================================================

				Recharge Number : %s
				Device          : %s
				Recharge Date   : %s

				Amount          : %s %s
				Status          : FAILED

				Reason          : %s

				============================================================

				If any amount was deducted from your account, please allow
				your payment provider to process the reversal/refund
				according to their normal processing time.

				You may try the recharge again after verifying your
				payment details.

				If you believe this failure occurred incorrectly, please
				contact our support team and provide the recharge number
				mentioned above.

				Regards,
				AMI Support Team
				""".formatted(customerName, rechargeNumber, deviceIdentifier, rechargeDate, currency, amount,
				normalizeFailureReason(failureReason));
	}

	// ============================================================
	// PDF RECEIPT
	// ============================================================

	private byte[] generateRechargeReceiptPdf(String customerName, String deviceIdentifier, String rechargeNumber,
			BigDecimal amount, BigDecimal creditedUnits, BigDecimal balanceBefore, BigDecimal balanceAfter,
			String paymentId, String paymentMethod, String currency, LocalDateTime rechargeDate) throws IOException {

		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream(); PDDocument document = new PDDocument()) {

			PDPage page = new PDPage(PDRectangle.A4);
			document.addPage(page);

			try (PDPageContentStream content = new PDPageContentStream(document, page)) {

				float pageWidth = page.getMediaBox().getWidth();

				// --------------------------------------------------------
				// HEADER
				// --------------------------------------------------------

				content.setNonStrokingColor(new Color(25, 55, 109));

				content.addRect(0, 730, pageWidth, 112);

				content.fill();

				content.setNonStrokingColor(Color.WHITE);

				content.beginText();
				content.setFont(PDType1Font.HELVETICA_BOLD, 24);
				content.newLineAtOffset(50, 795);
				content.showText("AMI");
				content.endText();

				content.beginText();
				content.setFont(PDType1Font.HELVETICA, 11);
				content.newLineAtOffset(50, 772);
				content.showText("Advanced Metering Infrastructure");
				content.endText();

				content.beginText();
				content.setFont(PDType1Font.HELVETICA_BOLD, 16);
				content.newLineAtOffset(390, 790);
				content.showText("RECHARGE RECEIPT");
				content.endText();

				// --------------------------------------------------------
				// SUCCESS STATUS
				// --------------------------------------------------------

				content.setNonStrokingColor(new Color(40, 167, 69));

				content.addRect(50, 685, 495, 35);

				content.fill();

				content.setNonStrokingColor(Color.WHITE);

				content.beginText();
				content.setFont(PDType1Font.HELVETICA_BOLD, 13);
				content.newLineAtOffset(225, 697);
				content.showText("PAYMENT SUCCESSFUL");
				content.endText();

				// --------------------------------------------------------
				// CUSTOMER INFORMATION
				// --------------------------------------------------------

				drawSectionTitle(content, "CUSTOMER & DEVICE INFORMATION", 650);

				drawRow(content, "Customer Name", customerName, 625);

				drawRow(content, "Device", deviceIdentifier, 602);

				drawRow(content, "Recharge Number", rechargeNumber, 579);

				drawRow(content, "Recharge Date", rechargeDate.toString(), 556);

				// --------------------------------------------------------
				// PAYMENT INFORMATION
				// --------------------------------------------------------

				drawSectionTitle(content, "PAYMENT INFORMATION", 515);

				drawRow(content, "Payment ID", paymentId, 490);

				drawRow(content, "Payment Method", paymentMethod, 467);

				drawRow(content, "Amount Paid", currency + " " + amount, 444);

				// --------------------------------------------------------
				// PREPAID BALANCE
				// --------------------------------------------------------

				drawSectionTitle(content, "PREPAID BALANCE", 403);

				drawRow(content, "Balance Before", balanceBefore.toPlainString(), 378);

				drawRow(content, "Units Credited", creditedUnits.toPlainString(), 355);

				drawRow(content, "Balance After", balanceAfter.toPlainString(), 332);

				// --------------------------------------------------------
				// FOOTER
				// --------------------------------------------------------

				content.setNonStrokingColor(new Color(100, 100, 100));

				content.beginText();
				content.setFont(PDType1Font.HELVETICA, 9);
				content.newLineAtOffset(50, 85);
				content.showText("This is a system-generated receipt and does not require a signature.");
				content.endText();

				content.beginText();
				content.newLineAtOffset(50, 68);
				content.showText("Please retain this receipt for your records.");
				content.endText();

				content.beginText();
				content.newLineAtOffset(50, 45);
				content.showText("AMI Support Team");
				content.endText();
			}

			document.save(outputStream);

			return outputStream.toByteArray();
		}
	}

	// ============================================================
	// PDF HELPER METHODS
	// ============================================================

	private void drawSectionTitle(PDPageContentStream content, String title, float y) throws IOException {

		content.setNonStrokingColor(new Color(25, 55, 109));

		content.beginText();
		content.setFont(PDType1Font.HELVETICA_BOLD, 11);
		content.newLineAtOffset(50, y);
		content.showText(title);
		content.endText();
	}

	private void drawRow(PDPageContentStream content, String label, String value, float y) throws IOException {

		content.setNonStrokingColor(Color.DARK_GRAY);

		content.beginText();
		content.setFont(PDType1Font.HELVETICA_BOLD, 10);
		content.newLineAtOffset(60, y);
		content.showText(label);
		content.endText();

		content.beginText();
		content.setFont(PDType1Font.HELVETICA, 10);
		content.newLineAtOffset(250, y);
		content.showText(value != null ? value : "-");
		content.endText();
	}

	private String normalizeFailureReason(String reason) {

		if (reason == null || reason.isBlank()) {
			return "Payment could not be verified.";
		}

		return reason;
	}

	@Override
	public void sendPrepaidLowBalanceEmail(String recipientEmail, String customerName, String deviceIdentifier,
			BigDecimal availableUnits, BigDecimal totalCreditedUnits, BigDecimal percentageRemaining) {

		String subject = "Low Prepaid Balance Alert – " + deviceIdentifier;

		String body = """
				<!DOCTYPE html>
				<html>
				<body style="font-family: Arial, sans-serif; color: #333; line-height: 1.6;">

				    <h2 style="color: #d97706;">Low Prepaid Balance Alert</h2>

				    <p>Dear %s,</p>

				    <p>
				        This is a notification that the prepaid balance for your device
				        <strong>%s</strong> has fallen below the low-balance threshold.
				    </p>

				    <table style="border-collapse: collapse; margin: 20px 0;">
				        <tr>
				            <td style="padding: 8px 20px 8px 0;"><strong>Device</strong></td>
				            <td style="padding: 8px;">%s</td>
				        </tr>
				        <tr>
				            <td style="padding: 8px 20px 8px 0;"><strong>Available Balance</strong></td>
				            <td style="padding: 8px;">%s units</td>
				        </tr>
				        <tr>
				            <td style="padding: 8px 20px 8px 0;"><strong>Original Credited Units</strong></td>
				            <td style="padding: 8px;">%s units</td>
				        </tr>
				        <tr>
				            <td style="padding: 8px 20px 8px 0;"><strong>Balance Remaining</strong></td>
				            <td style="padding: 8px;">%s%%</td>
				        </tr>
				    </table>

				    <p>
				        We recommend recharging your prepaid balance soon to avoid
				        interruption of service.
				    </p>

				    <p>
				        You can recharge your device from your AMI account.
				    </p>

				    <p>Regards,<br><strong>AMI Billing Team</strong></p>

				</body>
				</html>
				""".formatted(customerName, deviceIdentifier, deviceIdentifier, availableUnits, totalCreditedUnits,
				percentageRemaining);

		try {
			MimeMessage message = mailSender.createMimeMessage();

			MimeMessageHelper helper = new MimeMessageHelper(message, false, StandardCharsets.UTF_8.name());

			helper.setFrom(fromEmail);
			helper.setTo(recipientEmail);
			helper.setSubject(subject);
			helper.setText(body, true);

			mailSender.send(message);

		} catch (MessagingException e) {
			throw new RuntimeException("Failed to send prepaid low balance email", e);
		}
	}

	@Override
	public void sendPrepaidVeryLowBalanceEmail(String recipientEmail, String customerName, String deviceIdentifier,
			BigDecimal availableUnits, BigDecimal totalCreditedUnits, BigDecimal percentageRemaining) {

		String subject = "URGENT: Very Low Prepaid Balance – " + deviceIdentifier;

		String body = """
				<!DOCTYPE html>
				<html>
				<body style="font-family: Arial, sans-serif; color: #333; line-height: 1.6;">

				    <h2 style="color: #b91c1c;">Very Low Prepaid Balance</h2>

				    <p>Dear %s,</p>

				    <p>
				        Your prepaid balance for device <strong>%s</strong> is now
				        critically low.
				    </p>

				    <table style="border-collapse: collapse; margin: 20px 0;">
				        <tr>
				            <td style="padding: 8px 20px 8px 0;"><strong>Device</strong></td>
				            <td style="padding: 8px;">%s</td>
				        </tr>
				        <tr>
				            <td style="padding: 8px 20px 8px 0;"><strong>Available Balance</strong></td>
				            <td style="padding: 8px;">%s units</td>
				        </tr>
				        <tr>
				            <td style="padding: 8px 20px 8px 0;"><strong>Original Credited Units</strong></td>
				            <td style="padding: 8px;">%s units</td>
				        </tr>
				        <tr>
				            <td style="padding: 8px 20px 8px 0;"><strong>Balance Remaining</strong></td>
				            <td style="padding: 8px;">%s%%</td>
				        </tr>
				    </table>

				    <p>
				        <strong>Immediate action is recommended.</strong>
				        Please recharge your prepaid balance to avoid possible
				        interruption of service.
				    </p>

				    <p>
				        You can recharge your device from your AMI account.
				    </p>

				    <p>Regards,<br><strong>AMI Billing Team</strong></p>

				</body>
				</html>
				""".formatted(customerName, deviceIdentifier, deviceIdentifier, availableUnits, totalCreditedUnits,
				percentageRemaining);

		try {
			MimeMessage message = mailSender.createMimeMessage();

			MimeMessageHelper helper = new MimeMessageHelper(message, false, StandardCharsets.UTF_8.name());

			helper.setFrom(fromEmail);
			helper.setTo(recipientEmail);
			helper.setSubject(subject);
			helper.setText(body, true);

			mailSender.send(message);

		} catch (MessagingException e) {
			throw new RuntimeException("Failed to send prepaid very low balance email", e);
		}
	}

}
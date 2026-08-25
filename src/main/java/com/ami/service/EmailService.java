package com.ami.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.ami.entity.Invoice;
import com.ami.entity.Payment;

public interface EmailService {

	void sendResetPasswordEmail(String toEmail, String firstName, String resetLink);

	void sendInvoiceGeneratedEmail(Invoice invoice, byte[] pdfBytes);

	void sendPaymentReceiptEmail(Payment payment, byte[] receiptPdfBytes);

	void sendRechargeSuccessEmail(String recipientEmail, String customerName, String deviceIdentifier,
			String rechargeNumber, BigDecimal amount, BigDecimal creditedUnits, BigDecimal balanceBefore,
			BigDecimal balanceAfter, String paymentId, String paymentMethod, String currency,
			LocalDateTime rechargeDate);

	void sendRechargeFailureEmail(String recipientEmail, String customerName, String deviceIdentifier,
			String rechargeNumber, BigDecimal amount, String currency, String failureReason,
			LocalDateTime rechargeDate);

	void sendPrepaidLowBalanceEmail(String recipientEmail, String customerName, String deviceIdentifier,
			BigDecimal availableUnits, BigDecimal totalCreditedUnits, BigDecimal percentageRemaining);

	void sendPrepaidVeryLowBalanceEmail(String recipientEmail, String customerName, String deviceIdentifier,
			BigDecimal availableUnits, BigDecimal totalCreditedUnits, BigDecimal percentageRemaining);

}
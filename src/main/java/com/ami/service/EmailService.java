package com.ami.service;

import com.ami.entity.Invoice;
import com.ami.entity.Payment;

public interface EmailService {

	void sendResetPasswordEmail(String toEmail, String firstName, String resetLink);

	void sendInvoiceGeneratedEmail(Invoice invoice, byte[] pdfBytes);
	
	void sendPaymentReceiptEmail(Payment payment, byte[] receiptPdfBytes);

}
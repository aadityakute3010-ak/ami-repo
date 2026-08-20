package com.ami.service;

import com.ami.entity.Payment;

public interface PaymentReceiptPdfService {

	byte[] generatePaymentReceiptPdf(Payment payment);
}
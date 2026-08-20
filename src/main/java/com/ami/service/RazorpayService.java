package com.ami.service;

import java.math.BigDecimal;
import java.util.Map;

public interface RazorpayService {

	RazorpayOrderResult createOrder(Long invoiceId, String invoiceNumber, BigDecimal amount);

	boolean verifySignature(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature);

	String getKeyId();
	
	RazorpayOrderResult createOrder(String receipt, BigDecimal amount, Map<String, String> notes);

	record RazorpayOrderResult(String orderId, String currency, BigDecimal amount, String status) {
	}
}
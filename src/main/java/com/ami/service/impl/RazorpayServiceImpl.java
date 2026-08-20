package com.ami.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.ami.config.RazorpayProperties;
import com.ami.service.RazorpayService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RazorpayServiceImpl implements RazorpayService {

	private final RazorpayProperties razorpayProperties;

	@Override
	public RazorpayOrderResult createOrder(Long invoiceId, String invoiceNumber, BigDecimal amount) {

		return createOrder(buildReceipt(invoiceId, invoiceNumber), amount, Map.of("type", "INVOICE_PAYMENT",
				"invoiceId", String.valueOf(invoiceId), "invoiceNumber", invoiceNumber));
	}

	@Override
	public RazorpayOrderResult createOrder(String receipt, BigDecimal amount, Map<String, String> notes) {

		try {
			RazorpayClient razorpayClient = new RazorpayClient(razorpayProperties.getKeyId(),
					razorpayProperties.getKeySecret());

			JSONObject orderRequest = new JSONObject();

			orderRequest.put("amount", convertRupeesToPaise(amount));
			orderRequest.put("currency", resolveCurrency());
			orderRequest.put("receipt", receipt);
			orderRequest.put("payment_capture", 1);

			JSONObject notesObject = new JSONObject();

			if (notes != null) {
				notes.forEach(notesObject::put);
			}

			orderRequest.put("notes", notesObject);

			Order order = razorpayClient.orders.create(orderRequest);

			return new RazorpayOrderResult(order.get("id"), order.get("currency"), amount, order.get("status"));

		} catch (Exception exception) {
			throw new IllegalStateException("Unable to create Razorpay order: " + exception.getMessage(), exception);
		}
	}

	@Override
	public boolean verifySignature(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) {

		if (razorpayOrderId == null || razorpayOrderId.isBlank()) {
			throw new IllegalArgumentException("Razorpay order id is required");
		}

		if (razorpayPaymentId == null || razorpayPaymentId.isBlank()) {
			throw new IllegalArgumentException("Razorpay payment id is required");
		}

		if (razorpaySignature == null || razorpaySignature.isBlank()) {
			throw new IllegalArgumentException("Razorpay signature is required");
		}

		try {
			String payload = razorpayOrderId + "|" + razorpayPaymentId;

			String generatedSignature = hmacSha256(payload, razorpayProperties.getKeySecret());

			return generatedSignature.equals(razorpaySignature);

		} catch (Exception exception) {
			throw new IllegalStateException("Unable to verify Razorpay payment signature", exception);
		}
	}

	@Override
	public String getKeyId() {
		return razorpayProperties.getKeyId();
	}

	private int convertRupeesToPaise(BigDecimal amount) {

		if (amount == null) {
			throw new IllegalArgumentException("Payment amount is required");
		}

		if (amount.compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("Payment amount must be greater than zero");
		}

		return amount.multiply(BigDecimal.valueOf(100)).setScale(0, RoundingMode.HALF_UP).intValueExact();
	}

	private String resolveCurrency() {

		if (razorpayProperties.getCurrency() == null || razorpayProperties.getCurrency().isBlank()) {
			return "INR";
		}

		return razorpayProperties.getCurrency().trim().toUpperCase();
	}

	private String buildReceipt(Long invoiceId, String invoiceNumber) {

		String safeInvoiceNumber = invoiceNumber == null ? "NA" : invoiceNumber;

		String receipt = "INV-" + invoiceId + "-" + safeInvoiceNumber;

		if (receipt.length() > 40) {
			return receipt.substring(0, 40);
		}

		return receipt;
	}

	private String hmacSha256(String data, String secret) throws Exception {

		Mac sha256Hmac = Mac.getInstance("HmacSHA256");

		SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");

		sha256Hmac.init(secretKey);

		byte[] hash = sha256Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));

		return bytesToHex(hash);
	}

	private String bytesToHex(byte[] bytes) {

		StringBuilder result = new StringBuilder();

		for (byte b : bytes) {
			result.append(String.format("%02x", b));
		}

		return result.toString();
	}

}
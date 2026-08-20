package com.ami.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ami.dto.requests.CreatePaymentOrderRequestDto;
import com.ami.dto.requests.CreatePaymentRequestDto;
import com.ami.dto.requests.VerifyPaymentRequestDto;
import com.ami.dto.responses.PagedPaymentResponseDto;
import com.ami.dto.responses.PaymentOrderResponseDto;
import com.ami.dto.responses.PaymentResponseDto;
import com.ami.entity.Invoice;
import com.ami.entity.Payment;
import com.ami.entity.User;
import com.ami.enums.InvoiceStatus;
import com.ami.enums.PaymentGateway;
import com.ami.enums.PaymentMethod;
import com.ami.enums.PaymentStatus;
import com.ami.enums.PaymentTransactionStatus;
import com.ami.enums.RoleType;
import com.ami.exception.ResourceNotFoundException;
import com.ami.repository.InvoiceRepository;
import com.ami.repository.PaymentRepository;
import com.ami.security.SecurityUtils;
import com.ami.service.PaymentService;
import com.ami.service.RazorpayService;
import lombok.RequiredArgsConstructor;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.ami.dto.responses.PaymentReceiptResponseDto;
import com.ami.service.EmailService;
import com.ami.service.PaymentReceiptPdfService;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

	private final PaymentRepository paymentRepository;

	private final InvoiceRepository invoiceRepository;

	private final SecurityUtils securityUtils;

	private final RazorpayService razorpayService;

	private final PaymentReceiptPdfService paymentReceiptPdfService;

	private final EmailService emailService;

	@Override
	@Transactional
	public PaymentResponseDto createPayment(CreatePaymentRequestDto request) {

		Invoice invoice = getInvoiceForPayment(request.getInvoiceId());

		validateInvoicePayable(invoice);

		validatePaymentAmount(request.getAmount(), invoice);

		User loggedInUser = securityUtils.getLoggedInUser();

		validatePaymentAccess(invoice, loggedInUser);

		Payment payment = Payment.builder().transactionId(generateTransactionId()).invoice(invoice)
				.customer(invoice.getCustomer()).customerName(invoice.getCustomerName()).amount(request.getAmount())
				.method(request.getMethod()).gateway(resolveGateway(request.getGateway(), request.getMethod()))
				.status(PaymentTransactionStatus.SUCCESS).referenceNumber(normalizeText(request.getReferenceNumber()))
				.remarks(normalizeText(request.getRemarks())).paymentDate(LocalDateTime.now())
				.source(invoice.getSource()).billingPeriod(buildBillingPeriod(invoice)).dueDate(invoice.getDueDate())
				.meterNumber(invoice.getMeterNumber()).createdBy(loggedInUser).build();
		payment.setCreatedAt(LocalDateTime.now());
		payment.setUpdatedAt(LocalDateTime.now());

		Payment savedPayment = paymentRepository.save(payment);

		applySuccessfulPayment(invoice, request.getAmount());

		sendPaymentReceiptEmailSafely(savedPayment);

		return mapToResponse(savedPayment);
	}

	@Override
	@Transactional
	public PaymentOrderResponseDto createPaymentOrder(CreatePaymentOrderRequestDto request) {

		Invoice invoice = getInvoiceForPayment(request.getInvoiceId());

		validateInvoicePayable(invoice);

		validatePaymentAmount(request.getAmount(), invoice);

		User loggedInUser = securityUtils.getLoggedInUser();

		validatePaymentAccess(invoice, loggedInUser);

		RazorpayService.RazorpayOrderResult razorpayOrder = razorpayService.createOrder(invoice.getId(),
				invoice.getInvoiceNumber(), request.getAmount());

		return PaymentOrderResponseDto.builder().orderId(razorpayOrder.orderId()).invoiceId(invoice.getId())
				.invoiceNumber(invoice.getInvoiceNumber()).amount(razorpayOrder.amount())
				.currency(razorpayOrder.currency()).gateway(PaymentGateway.RAZORPAY.name())
				.status(razorpayOrder.status()).keyId(razorpayService.getKeyId()).build();
	}

	@Override
	@Transactional
	public PaymentResponseDto verifyPayment(VerifyPaymentRequestDto request) {

		Invoice invoice = getInvoiceForPayment(request.getInvoiceId());

		validateInvoicePayable(invoice);

		validatePaymentAmount(request.getAmount(), invoice);

		User loggedInUser = securityUtils.getLoggedInUser();

		validatePaymentAccess(invoice, loggedInUser);

		if (paymentRepository.existsByRazorpayPaymentId(request.getRazorpayPaymentId())) {
			throw new IllegalArgumentException("Razorpay payment is already verified");
		}

		boolean validSignature = razorpayService.verifySignature(request.getRazorpayOrderId(),
				request.getRazorpayPaymentId(), request.getRazorpaySignature());

		if (!validSignature) {
			throw new IllegalArgumentException("Invalid Razorpay payment signature");
		}

		Payment payment = Payment.builder().transactionId(generateTransactionId()).invoice(invoice)
				.customer(invoice.getCustomer()).customerName(invoice.getCustomerName()).amount(request.getAmount())
				.method(request.getMethod()).gateway(PaymentGateway.RAZORPAY).status(PaymentTransactionStatus.SUCCESS)
				.referenceNumber(request.getRazorpayPaymentId()).remarks(normalizeText(request.getRemarks()))
				.paymentDate(LocalDateTime.now()).razorpayOrderId(request.getRazorpayOrderId())
				.razorpayPaymentId(request.getRazorpayPaymentId()).razorpaySignature(request.getRazorpaySignature())
				.source(invoice.getSource()).billingPeriod(buildBillingPeriod(invoice)).dueDate(invoice.getDueDate())
				.meterNumber(invoice.getMeterNumber()).createdBy(loggedInUser).build();

		payment.setCreatedAt(LocalDateTime.now());
		payment.setUpdatedAt(LocalDateTime.now());

		Payment savedPayment = paymentRepository.save(payment);

		applySuccessfulPayment(invoice, request.getAmount());

		sendPaymentReceiptEmailSafely(savedPayment);

		return mapToResponse(savedPayment);
	}

	@Override
	@Transactional(readOnly = true)
	public PaymentReceiptResponseDto getPaymentReceiptDetails(Long paymentId) {

		Payment payment = paymentRepository.findById(paymentId)
				.orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + paymentId));

		validatePaymentReadAccess(payment);

		return mapToReceiptResponse(payment);
	}

	@Override
	@Transactional(readOnly = true)
	public ResponseEntity<ByteArrayResource> generatePaymentReceiptPdfResponse(Long paymentId, boolean download) {

		Payment payment = paymentRepository.findById(paymentId)
				.orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + paymentId));

		validatePaymentReadAccess(payment);

		byte[] pdfBytes = paymentReceiptPdfService.generatePaymentReceiptPdf(payment);

		ByteArrayResource resource = new ByteArrayResource(pdfBytes);

		String fileName = "Payment-Receipt-" + payment.getTransactionId() + ".pdf";

		ContentDisposition contentDisposition = download ? ContentDisposition.attachment().filename(fileName).build()
				: ContentDisposition.inline().filename(fileName).build();

		return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF)
				.header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString()).contentLength(pdfBytes.length)
				.body(resource);
	}

	private void sendPaymentReceiptEmailSafely(Payment payment) {

		try {
			byte[] receiptPdfBytes = paymentReceiptPdfService.generatePaymentReceiptPdf(payment);

			emailService.sendPaymentReceiptEmail(payment, receiptPdfBytes);

		} catch (Exception exception) {
			System.err.println(
					"Payment receipt email failed for payment id " + payment.getId() + ": " + exception.getMessage());
		}
	}

	private PaymentReceiptResponseDto mapToReceiptResponse(Payment payment) {

		Invoice invoice = payment.getInvoice();

		return PaymentReceiptResponseDto.builder().paymentId(payment.getId()).receiptNumber(buildReceiptNumber(payment))
				.transactionId(payment.getTransactionId()).paymentDate(payment.getPaymentDate())
				.amountPaid(payment.getAmount()).paymentMethod(payment.getMethod()).paymentGateway(payment.getGateway())
				.paymentStatus(payment.getStatus()).referenceNumber(payment.getReferenceNumber())
				.remarks(payment.getRemarks()).invoiceId(invoice != null ? invoice.getId() : null)
				.invoiceNumber(invoice != null ? invoice.getInvoiceNumber() : null)
				.invoiceNetAmount(invoice != null ? invoice.getNetAmount() : null)
				.invoicePaidAmount(invoice != null ? invoice.getPaidAmount() : null)
				.invoiceBalanceAmount(invoice != null ? invoice.getBalanceAmount() : null)
				.invoicePaymentStatus(
						invoice != null && invoice.getPaymentStatus() != null ? invoice.getPaymentStatus().name()
								: null)
				.customerName(payment.getCustomerName()).customerEmail(invoice != null ? invoice.getEmail() : null)
				.customerPhone(invoice != null ? invoice.getPhone() : null).source(payment.getSource())
				.meterNumber(payment.getMeterNumber()).billingPeriod(payment.getBillingPeriod())
				.dueDate(payment.getDueDate())
				.receiptPreviewUrl(
						payment.getId() != null ? "/api/billing/payments/" + payment.getId() + "/receipt/preview"
								: null)
				.receiptDownloadUrl(
						payment.getId() != null ? "/api/billing/payments/" + payment.getId() + "/receipt/download"
								: null)
				.build();
	}

	private String buildReceiptNumber(Payment payment) {

		if (payment.getTransactionId() == null) {
			return "RCPT-" + payment.getId();
		}

		return "RCPT-" + payment.getTransactionId();
	}

	@Override
	@Transactional(readOnly = true)
	public PaymentResponseDto getPaymentById(Long paymentId) {

		Payment payment = paymentRepository.findById(paymentId)
				.orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + paymentId));

		validatePaymentReadAccess(payment);

		return mapToResponse(payment);
	}

	@Override
	@Transactional(readOnly = true)
	public List<PaymentResponseDto> getPaymentsByInvoice(Long invoiceId) {

		Invoice invoice = getInvoiceForPayment(invoiceId);

		User loggedInUser = securityUtils.getLoggedInUser();

		validatePaymentAccess(invoice, loggedInUser);

		return paymentRepository.findByInvoiceOrderByPaymentDateDesc(invoice).stream().map(this::mapToResponse)
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public PagedPaymentResponseDto getPayments(int page, int size, String search, PaymentTransactionStatus status,
			String fromDate, String toDate) {

		String normalizedSearch = search == null || search.isBlank() ? null : search.trim();

		LocalDate parsedFromDate = parseDate(fromDate, "fromDate");

		LocalDate parsedToDate = parseDate(toDate, "toDate");

		LocalDateTime fromDateTime = parsedFromDate == null ? null : parsedFromDate.atStartOfDay();

		LocalDateTime toDateTime = parsedToDate == null ? null : parsedToDate.atTime(LocalTime.MAX);

		Pageable pageable = PageRequest.of(page, size);

		Page<Payment> paymentPage = paymentRepository.findPaymentsWithFilters(normalizedSearch, status, fromDateTime,
				toDateTime, pageable);

		return PagedPaymentResponseDto.builder()
				.payments(paymentPage.getContent().stream().map(this::mapToResponse).toList())
				.currentPage(paymentPage.getNumber()).totalPages(paymentPage.getTotalPages())
				.totalElements(paymentPage.getTotalElements()).build();
	}

	private Invoice getInvoiceForPayment(Long invoiceId) {

		return invoiceRepository.findById(invoiceId)
				.orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + invoiceId));
	}

	private void validateInvoicePayable(Invoice invoice) {

		if (invoice.getStatus() == InvoiceStatus.FAILED) {
			throw new IllegalStateException("Payment cannot be recorded for failed invoice");
		}

		if (invoice.getPaymentStatus() == PaymentStatus.PAID) {
			throw new IllegalStateException("Invoice is already fully paid");
		}

		BigDecimal balanceAmount = defaultZero(invoice.getBalanceAmount());

		if (balanceAmount.compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalStateException("Invoice has no pending balance");
		}
	}

	private void validatePaymentAmount(BigDecimal amount, Invoice invoice) {

		if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("Payment amount must be greater than zero");
		}

		BigDecimal balanceAmount = defaultZero(invoice.getBalanceAmount());

		if (amount.compareTo(balanceAmount) > 0) {
			throw new IllegalArgumentException("Payment amount cannot be greater than invoice balance amount");
		}
	}

	private void validatePaymentAccess(Invoice invoice, User loggedInUser) {

		if (loggedInUser.getRole() == RoleType.SUPER_ADMIN) {
			return;
		}

		if (loggedInUser.getRole() == RoleType.ADMIN) {

			if (invoice.getDevice() == null || invoice.getDevice().getAssignedAdmin() == null) {
				throw new IllegalStateException("Invoice device admin is not available");
			}

			if (!invoice.getDevice().getAssignedAdmin().getId().equals(loggedInUser.getId())) {
				throw new IllegalArgumentException("You are not allowed to access this invoice payment");
			}

			return;
		}

		if (loggedInUser.getRole() == RoleType.USER) {

			if (invoice.getCustomer() == null || !invoice.getCustomer().getId().equals(loggedInUser.getId())) {
				throw new IllegalArgumentException("You are not allowed to access this invoice payment");
			}

			return;
		}

		throw new IllegalArgumentException("You are not allowed to access this invoice payment");
	}

	private void validatePaymentReadAccess(Payment payment) {

		if (payment.getInvoice() == null) {
			throw new IllegalStateException("Payment invoice is not available");
		}

		validatePaymentAccess(payment.getInvoice(), securityUtils.getLoggedInUser());
	}

	private void applySuccessfulPayment(Invoice invoice, BigDecimal amount) {

		BigDecimal paidAmount = defaultZero(invoice.getPaidAmount()).add(amount);

		BigDecimal balanceAmount = defaultZero(invoice.getBalanceAmount()).subtract(amount);

		if (balanceAmount.compareTo(BigDecimal.ZERO) < 0) {
			balanceAmount = BigDecimal.ZERO;
		}

		invoice.setPaidAmount(paidAmount);
		invoice.setBalanceAmount(balanceAmount);

		if (balanceAmount.compareTo(BigDecimal.ZERO) == 0) {
			invoice.setPaymentStatus(PaymentStatus.PAID);
			invoice.setStatus(InvoiceStatus.PAID);
		} else {
			invoice.setPaymentStatus(PaymentStatus.PARTIAL);
		}

		invoice.setUpdatedAt(LocalDateTime.now());

		invoiceRepository.save(invoice);
	}

	private PaymentGateway resolveGateway(PaymentGateway gateway, PaymentMethod method) {

		if (gateway != null) {
			return gateway;
		}

		if (method == PaymentMethod.CASH) {
			return PaymentGateway.CASH;
		}

		return null;
	}

	private PaymentResponseDto mapToResponse(Payment payment) {

		Invoice invoice = payment.getInvoice();

		return PaymentResponseDto.builder().id(payment.getId()).transactionId(payment.getTransactionId())
				.invoiceId(invoice != null ? invoice.getId() : null)
				.invoiceNumber(invoice != null ? invoice.getInvoiceNumber() : null)
				.customerId(payment.getCustomer() != null ? payment.getCustomer().getId() : null)
				.customerName(payment.getCustomerName()).amount(payment.getAmount()).method(payment.getMethod())
				.status(payment.getStatus()).referenceNumber(payment.getReferenceNumber()).remarks(payment.getRemarks())
				.paymentDate(payment.getPaymentDate()).createdAt(payment.getCreatedAt()).gateway(payment.getGateway())
				.razorpayOrderId(payment.getRazorpayOrderId()).razorpayPaymentId(payment.getRazorpayPaymentId())
				.razorpaySignature(payment.getRazorpaySignature()).source(payment.getSource())
				.billingPeriod(payment.getBillingPeriod()).dueDate(payment.getDueDate())
				.meterNumber(payment.getMeterNumber())
				.invoicePaidAmount(invoice != null ? invoice.getPaidAmount() : null)
				.invoiceBalanceAmount(invoice != null ? invoice.getBalanceAmount() : null)
				.invoicePaymentStatus(
						invoice != null && invoice.getPaymentStatus() != null ? invoice.getPaymentStatus().name()
								: null)
				.build();
	}

	private String buildBillingPeriod(Invoice invoice) {

		if (invoice.getBillingPeriodFrom() == null || invoice.getBillingPeriodTo() == null) {
			return null;
		}

		return invoice.getBillingPeriodFrom() + " to " + invoice.getBillingPeriodTo();
	}

	private String generateTransactionId() {

		return "TXN-" + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + "-"
				+ UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
	}

	private String normalizeText(String value) {

		if (value == null || value.isBlank()) {
			return null;
		}

		return value.trim();
	}

	private BigDecimal defaultZero(BigDecimal value) {

		return value == null ? BigDecimal.ZERO : value;
	}

	private LocalDate parseDate(String date, String fieldName) {

		if (date == null || date.isBlank()) {
			return null;
		}

		try {
			return LocalDate.parse(date.trim());
		} catch (DateTimeParseException exception) {
			throw new IllegalArgumentException(fieldName + " must be in yyyy-MM-dd format");
		}
	}
}
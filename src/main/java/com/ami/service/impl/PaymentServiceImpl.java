package com.ami.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ami.dto.requests.CreatePaymentRequestDto;
import com.ami.dto.responses.PaymentResponseDto;
import com.ami.entity.Invoice;
import com.ami.entity.Payment;
import com.ami.enums.PaymentStatus;
import com.ami.repository.InvoiceRepository;
import com.ami.repository.PaymentRepository;
import com.ami.service.PaymentService;



@Service
public class PaymentServiceImpl
        implements PaymentService {

    private final PaymentRepository paymentRepository;

    private final InvoiceRepository invoiceRepository;

    public PaymentServiceImpl(
            PaymentRepository paymentRepository,
            InvoiceRepository invoiceRepository) {

        this.paymentRepository = paymentRepository;
        this.invoiceRepository = invoiceRepository;
    }

    @Override
    public PaymentResponseDto createPayment(
            CreatePaymentRequestDto request) {

        Invoice invoice =
                invoiceRepository.findById(
                        request.getInvoiceId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Invoice not found"));

        Payment payment =
                Payment.builder()
                        .invoiceId(
                                invoice.getId())
                        .transactionId(
                                "TXN-" +
                                System.currentTimeMillis())
                        .customerId(
                                invoice.getCustomerId())
                        .customerName(
                                invoice.getCustomerName())
                        .amount(
                                request.getAmount())
                        .paymentMethod(
                                request.getPaymentMethod())
                        .remarks(
                                request.getRemarks())
                        .paymentDate(
                                LocalDateTime.now())
                        .createdAt(
                                LocalDateTime.now())
                        .build();

        payment =
                paymentRepository.save(payment);

        Double newPaidAmount =
                invoice.getPaidAmount()
                + request.getAmount();

        invoice.setPaidAmount(
                newPaidAmount);

        invoice.setBalanceAmount(
                invoice.getNetAmount()
                - newPaidAmount);

        if(invoice.getBalanceAmount() <= 0) {

            invoice.setPaymentStatus(
                    PaymentStatus.PAID);

        } else if(newPaidAmount > 0) {

            invoice.setPaymentStatus(
                    PaymentStatus.PARTIAL);

        }

        invoice.setHistory(
                invoice.getHistory()
                + "\n[" + LocalDateTime.now()
                + "] Payment Received : ₹"
                + request.getAmount());

        invoiceRepository.save(invoice);

        return mapToResponse(payment);
    }

    @Override
    public List<PaymentResponseDto>
    getAllPayments() {

        return paymentRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public PaymentResponseDto
    getPaymentById(Long id) {

        Payment payment =
                paymentRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Payment not found"));

        return mapToResponse(payment);
    }

    @Override
    public List<PaymentResponseDto>
    getPaymentsByInvoiceId(
            Long invoiceId) {

        return paymentRepository
                .findByInvoiceId(invoiceId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private PaymentResponseDto
    mapToResponse(
            Payment payment) {

        return PaymentResponseDto
                .builder()
                .id(payment.getId())
                .invoiceId(
                        payment.getInvoiceId())
                .transactionId(
                        payment.getTransactionId())
                .customerName(
                        payment.getCustomerName())
                .amount(
                        payment.getAmount())
                .paymentMethod(
                        payment.getPaymentMethod())
                .paymentDate(
                        payment.getPaymentDate())
                .build();
    }
}
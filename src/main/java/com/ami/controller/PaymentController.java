package com.ami.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.ami.dto.requests.CreatePaymentRequestDto;
import com.ami.dto.responses.PaymentResponseDto;
import com.ami.service.PaymentService;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(
            PaymentService paymentService) {

        this.paymentService = paymentService;
    }
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @PostMapping
    public PaymentResponseDto createPayment(
            @RequestBody
            CreatePaymentRequestDto request) {

        return paymentService
                .createPayment(request);
    }
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','SERVICE_ENGINEER')")
    @GetMapping
    public List<PaymentResponseDto>
    getAllPayments() {

        return paymentService
                .getAllPayments();
    }
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','SERVICE_ENGINEER')")
    @GetMapping("/{id}")
    public PaymentResponseDto getPaymentById(
            @PathVariable Long id) {

        return paymentService
                .getPaymentById(id);
    }
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','SERVICE_ENGINEER')")
    @GetMapping("/invoice/{invoiceId}")
    public List<PaymentResponseDto>
    getPaymentsByInvoiceId(
            @PathVariable Long invoiceId) {

        return paymentService
                .getPaymentsByInvoiceId(
                        invoiceId);
    }
}
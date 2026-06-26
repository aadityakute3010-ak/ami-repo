package com.ami.service;

import java.util.List;

import com.ami.dto.requests.CreatePaymentRequestDto;
import com.ami.dto.responses.PaymentResponseDto;

public interface PaymentService {

    PaymentResponseDto createPayment(
            CreatePaymentRequestDto request);

    List<PaymentResponseDto> getAllPayments();

    PaymentResponseDto getPaymentById(
            Long id);

    List<PaymentResponseDto>
    getPaymentsByInvoiceId(
            Long invoiceId);
}
package com.ami.dto.requests;

import lombok.Data;

@Data
public class CreatePaymentRequestDto {

    private Long invoiceId;

    private Double amount;

    private String paymentMethod;

    private String remarks;
}
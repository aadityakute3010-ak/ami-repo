package com.ami.dto.responses;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentResponseDto {

    private Long id;

    private Long invoiceId;

    private String transactionId;

    private String customerName;

    private Double amount;

    private String paymentMethod;

    private LocalDateTime paymentDate;
}
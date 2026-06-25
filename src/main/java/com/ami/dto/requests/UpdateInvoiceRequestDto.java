package com.ami.dto.requests;

import java.time.LocalDate;

import com.ami.enums.InvoiceStatus;
import com.ami.enums.PaymentStatus;

import lombok.Data;

@Data
public class UpdateInvoiceRequestDto {

    private InvoiceStatus status;

    private PaymentStatus paymentStatus;

    private Double paidAmount;

    private String remarks;

    private LocalDate dueDate;
}
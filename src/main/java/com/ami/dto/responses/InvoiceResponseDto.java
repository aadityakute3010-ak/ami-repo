package com.ami.dto.responses;

import java.time.LocalDate;

import com.ami.enums.BillingType;
import com.ami.enums.InvoiceStatus;
import com.ami.enums.PaymentStatus;
import com.ami.enums.SourceType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InvoiceResponseDto {

    private Long id;

    private String invoiceNumber;

    private String customerId;

    private String customerName;

    private String meterNumber;

    private SourceType source;

    private BillingType billingType;

    private Double netAmount;

    private InvoiceStatus status;

    private PaymentStatus paymentStatus;

    private LocalDate invoiceDate;

    private LocalDate dueDate;

    private Double paidAmount;

    private Double balanceAmount;
    
    private String history;
}
package com.ami.dto.requests;

import java.time.LocalDate;

import com.ami.enums.BillingType;
import com.ami.enums.SourceType;

import lombok.Data;

@Data
public class CreateInvoiceRequestDto {

    private String customerId;

    private String customerName;

    private String email;

    private String phone;

    private String meterNumber;

    private SourceType source;

    private BillingType billingType;

    private Long tariffId;

    private Double previousReading;

    private Double currentReading;

    private Double fixedCharge;

    private Double tax;

    private Double discount;

    private LocalDate invoiceDate;

    private LocalDate dueDate;

    private LocalDate billingPeriodFrom;

    private LocalDate billingPeriodTo;

    private String remarks;
}
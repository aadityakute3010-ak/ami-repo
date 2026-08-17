package com.ami.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.ami.enums.BillingType;
import com.ami.enums.InvoiceStatus;
import com.ami.enums.PaymentStatus;
import com.ami.enums.SourceType;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "invoices")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String invoiceNumber;

    private String customerId;

    private String customerName;

    private String email;

    private String phone;

    private String meterNumber;

    @Enumerated(EnumType.STRING)
    private SourceType source;

    @Enumerated(EnumType.STRING)
    private BillingType billingType;

    private Long tariffId;

    private Double previousReading;

    private Double currentReading;

    private Double consumption;

    private Double amount;

    private Double fixedCharge;

    private Double tax;

    private Double discount;

    private Double netAmount;

    @Enumerated(EnumType.STRING)
    private InvoiceStatus status;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    private LocalDate invoiceDate;

    private LocalDate dueDate;

    private LocalDate billingPeriodFrom;

    private LocalDate billingPeriodTo;

    private String remarks;

    private Double paidAmount;

    private Double balanceAmount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
    
    @Column(length = 5000)
    private String history;

    private String generatedBy;

    private String lastUpdatedBy;
}
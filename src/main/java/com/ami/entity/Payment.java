package com.ami.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "payments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long invoiceId;

    private String transactionId;

    private String customerId;

    private String customerName;

    private Double amount;

    private String paymentMethod;

    private String remarks;

    private LocalDateTime paymentDate;

    private LocalDateTime createdAt;
}
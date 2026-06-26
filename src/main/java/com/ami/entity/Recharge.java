package com.ami.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "recharges")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Recharge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String rechargeNumber;

    private String customerId;

    private String customerName;

    private String meterNumber;

    private Double amount;

    private Double unitsAdded;

    private String paymentMethod;

    private String remarks;

    private LocalDateTime rechargeDate;

    private LocalDateTime createdAt;
}
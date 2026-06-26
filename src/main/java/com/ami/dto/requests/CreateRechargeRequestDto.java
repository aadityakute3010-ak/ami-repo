package com.ami.dto.requests;

import lombok.Data;

@Data
public class CreateRechargeRequestDto {

    private String customerId;

    private String customerName;

    private String meterNumber;

    private Double amount;

    private Double unitsAdded;

    private String paymentMethod;

    private String remarks;
}
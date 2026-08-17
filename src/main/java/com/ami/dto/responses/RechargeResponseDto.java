package com.ami.dto.responses;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RechargeResponseDto {

    private Long id;

    private String rechargeNumber;

    private String customerId;

    private String customerName;

    private Double amount;

    private Double unitsAdded;

    private LocalDateTime rechargeDate;
}
package com.ami.dto.responses;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RevenueTrendResponseDto {

    private int month;

    private String monthName;

    private BigDecimal revenue;

    private BigDecimal collected;

    private BigDecimal pending;
    
    private BigDecimal overdue;
}
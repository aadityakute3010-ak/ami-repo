package com.ami.dto.responses;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PagedPaymentResponseDto {

    private List<PaymentResponseDto> payments;

    private int currentPage;

    private int totalPages;

    private long totalElements;
}
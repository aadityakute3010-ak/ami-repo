package com.ami.dto.responses;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PagedInvoiceResponseDto {

	private List<InvoiceResponseDto> invoices;

	private int currentPage;

	private int totalPages;

	private long totalElements;
}
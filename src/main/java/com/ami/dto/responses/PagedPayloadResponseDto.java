package com.ami.dto.responses;

import java.util.List;

import lombok.Data;

@Data
public class PagedPayloadResponseDto {

    private List<PayloadSummaryDTO> payloads;

    private int currentPage;

    private int totalPages;

    private long totalElements;
}
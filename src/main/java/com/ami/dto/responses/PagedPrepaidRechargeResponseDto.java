package com.ami.dto.responses;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PagedPrepaidRechargeResponseDto {

	private List<PrepaidRechargeResponseDto> recharges;

	private int currentPage;

	private int totalPages;

	private long totalElements;
}
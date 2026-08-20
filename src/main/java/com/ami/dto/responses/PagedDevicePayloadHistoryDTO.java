package com.ami.dto.responses;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagedDevicePayloadHistoryDTO {

	private List<DevicePayloadHistoryDTO> history;

	private int currentPage;

	private int pageSize;

	private int totalPages;

	private long totalElements;

	private int currentElements;

	private boolean first;

	private boolean last;

	private boolean hasNext;

	private boolean hasPrevious;
}
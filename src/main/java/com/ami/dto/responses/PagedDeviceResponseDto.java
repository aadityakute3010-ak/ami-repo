package com.ami.dto.responses;

import java.util.List;

import lombok.Data;

@Data
public class PagedDeviceResponseDto {

    private List<DeviceListResponseDto> devices;

    private int currentPage;

    private int totalPages;

    private long totalElements;
} 
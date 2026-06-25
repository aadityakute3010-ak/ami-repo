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
public class DeviceBulkUploadResponseDto {

    private int totalRecords;

    private int successCount;

    private int failedCount;

    private List<String> errors;
}

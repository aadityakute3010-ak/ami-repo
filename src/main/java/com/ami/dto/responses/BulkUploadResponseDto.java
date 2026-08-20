package com.ami.dto.responses;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BulkUploadResponseDto {

    private int totalRecords;

    private int successCount;

    private int failedCount;

    private List<String> errors;
}

package com.ami.dto.requests;

import java.time.LocalDate;

import com.ami.enums.SourceType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportFilterRequestDto {

    private String deviceId;

    private SourceType sourceType;

    private String location;

    private String zone;

    private LocalDate fromDate;

    private LocalDate toDate;

    private String reportType;
}
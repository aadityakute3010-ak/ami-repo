package com.ami.dto.requests;

import java.time.LocalDate;

import lombok.Data;

@Data
public class ApplyLeaveRequestDto {

    private LocalDate fromDate;

    private LocalDate toDate;

    private String reason;
}
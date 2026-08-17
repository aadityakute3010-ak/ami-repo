package com.ami.dto.responses;

import java.time.LocalDate;

import com.ami.enums.LeaveStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EngineerLeaveResponseDto {

    private Long id;

    private LocalDate fromDate;

    private LocalDate toDate;

    private String reason;

    private LeaveStatus status;
}
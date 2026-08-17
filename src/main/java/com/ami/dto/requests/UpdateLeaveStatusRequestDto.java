package com.ami.dto.requests;

import com.ami.enums.LeaveStatus;

import lombok.Data;

@Data
public class UpdateLeaveStatusRequestDto {

    private LeaveStatus status;
}

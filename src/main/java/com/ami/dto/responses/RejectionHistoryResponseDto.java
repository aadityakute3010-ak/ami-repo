package com.ami.dto.responses;

import lombok.Data;

@Data
public class RejectionHistoryResponseDto {

    private Long issueId;
    private String rejectionHistory;
}
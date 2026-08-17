package com.ami.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignedAdminSummaryDto {

    private Long adminId;

    private String adminName;

    private String adminEmail;

    private String status;
}
package com.ami.dto.responses;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstallationEngineerResponseDto {

    private Long engineerId;

    private String engineerName;

    private String engineerEmail;

    private String engineerPhone;

    private LocalDateTime assignedAt;

}
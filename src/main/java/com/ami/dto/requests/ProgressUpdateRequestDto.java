package com.ami.dto.requests;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressUpdateRequestDto {

    @NotNull
    private Integer progressPercentage;

    @NotBlank
    private String remarks;

    @NotBlank
    private String currentWork;

    @NotNull
    private LocalDateTime estimatedCompletion;

    @NotBlank
    private String updatedBy;
    
    private Boolean workStarted;
}
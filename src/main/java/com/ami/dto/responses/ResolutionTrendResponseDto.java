package com.ami.dto.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResolutionTrendResponseDto {

    private String date;

    private Long resolvedCount;
}
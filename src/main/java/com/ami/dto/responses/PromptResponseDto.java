package com.ami.dto.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PromptResponseDto {

    private Long id;

    private String title;

    private String promptText;

    private String module;

    private Boolean active;
}
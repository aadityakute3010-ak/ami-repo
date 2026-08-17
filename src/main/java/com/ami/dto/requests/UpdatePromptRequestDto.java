package com.ami.dto.requests;

import lombok.Data;

@Data
public class UpdatePromptRequestDto {

    private String title;

    private String promptText;

    private String module;

    private Boolean active;
}
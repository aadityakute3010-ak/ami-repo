package com.ami.dto.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RejectIssueRequestDto {

    @NotBlank
    private String reason;

    private String comment;
}
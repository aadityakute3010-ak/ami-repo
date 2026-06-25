package com.ami.dto.requests;

import lombok.Data;

@Data
public class ResolveIssueRequestDto {

    private String rootCause;

    private String actionTaken;

    private String resolutionNotes;
}
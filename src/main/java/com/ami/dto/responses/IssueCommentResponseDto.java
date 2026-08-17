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
public class IssueCommentResponseDto {

    private Long id;

    private String comment;

    private String commentedBy;

    private String role;

    private LocalDateTime createdAt;
}
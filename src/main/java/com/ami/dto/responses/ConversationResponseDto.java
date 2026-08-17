package com.ami.dto.responses;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConversationResponseDto {

    private Long id;

    private String userId;

    private String question;

    private String answer;

    private String module;

    private LocalDateTime createdAt;
}
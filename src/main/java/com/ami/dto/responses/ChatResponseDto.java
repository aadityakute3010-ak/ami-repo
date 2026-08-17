package com.ami.dto.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatResponseDto {

    private Long conversationId;

    private String question;

    private String answer;

    private String module;
}
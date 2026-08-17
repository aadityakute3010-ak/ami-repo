package com.ami.service;

import java.util.List;

import com.ami.dto.requests.CreateChatRequestDto;
import com.ami.dto.responses.ChatResponseDto;
import com.ami.dto.responses.ConversationResponseDto;

public interface AiChatService {

    ChatResponseDto askQuestion(
            CreateChatRequestDto request);

    List<ConversationResponseDto>
    getAllConversations();

    ConversationResponseDto
    getConversationById(
            Long id);

    String deleteConversation(
            Long id);
}
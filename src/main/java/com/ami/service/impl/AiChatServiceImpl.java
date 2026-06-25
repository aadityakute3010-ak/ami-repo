package com.ami.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ami.dto.requests.CreateChatRequestDto;
import com.ami.dto.responses.ChatResponseDto;
import com.ami.dto.responses.ConversationResponseDto;
import com.ami.entity.AiAnalytics;
import com.ami.entity.ChatConversation;
import com.ami.entity.PromptTemplate;
import com.ami.repository.AiAnalyticsRepository;
import com.ami.repository.ChatConversationRepository;
import com.ami.repository.PromptTemplateRepository;
import com.ami.service.AiChatService;
import com.ami.service.GroqService;

@Service
public class AiChatServiceImpl
implements AiChatService {
	
private final ChatConversationRepository repository;

private final GroqService groqService;

private final PromptTemplateRepository promptRepository;

private final AiAnalyticsRepository analyticsRepository;

public AiChatServiceImpl(
        ChatConversationRepository repository,
        GroqService groqService,
        PromptTemplateRepository promptRepository,
        AiAnalyticsRepository analyticsRepository) {

    this.repository = repository;
    this.groqService = groqService;
    this.promptRepository = promptRepository;
    this.analyticsRepository = analyticsRepository;
}

@Override
public ChatResponseDto askQuestion(
        CreateChatRequestDto request) {

	String systemPrompt =
	        promptRepository
	                .findByModule(
	                        request.getModule())
	                .stream()
	                .filter(
	                        PromptTemplate::getActive)
	                .findFirst()
	                .map(
	                        PromptTemplate::getPromptText)
	                .orElse(
	                        "You are an AI assistant for the AMI utility management platform. "
	                        + "Provide accurate, concise, and professional responses.");

	String finalPrompt =
	        systemPrompt
	        + "\n\nModule: "
	        + request.getModule()
	        + "\nUser Question: "
	        + request.getMessage()
	        + "\n\nProvide a clear and professional answer.";

    long startTime =
            System.currentTimeMillis();
    
    if (request.getMessage() == null
            || request.getMessage().isBlank()) {

        throw new RuntimeException(
                "Question cannot be empty");
    }

    String answer =
            groqService.generateResponse(
                    finalPrompt);

    long responseTime =
            System.currentTimeMillis()
                    - startTime;

    ChatConversation chat =
            ChatConversation.builder()
                    .userId(
                            request.getUserId())
                    .userMessage(
                            request.getMessage())
                    .aiResponse(
                            answer)
                    .module(
                            request.getModule())
                    .systemPrompt(
                            systemPrompt)
                    .responseTimeMs(
                            responseTime)
                    .build();

    chat =
            repository.save(chat);

    AiAnalytics analytics =
            AiAnalytics.builder()
                    .userId(
                            request.getUserId())
                    .query(
                            request.getMessage())
                    .module(
                            request.getModule())
                    .responseTime(
                            responseTime)
                    .feedback(
                            "PENDING")
                    .build();

    analyticsRepository.save(
            analytics);

    return ChatResponseDto
            .builder()
            .conversationId(
                    chat.getId())
            .question(
                    chat.getUserMessage())
            .answer(
                    chat.getAiResponse())
            .module(
                    chat.getModule())
            .build();
}

@Override
public List<ConversationResponseDto>
getAllConversations() {

    return repository.findAll()
            .stream()
            .map(chat ->
                    ConversationResponseDto
                            .builder()
                            .id(chat.getId())
                            .userId(
                                    chat.getUserId())
                            .question(
                                    chat.getUserMessage())
                            .answer(
                                    chat.getAiResponse())
                            .module(
                                    chat.getModule())
                            .createdAt(
                                    chat.getCreatedAt())
                            .build())
            .toList();
}

@Override
public ConversationResponseDto
getConversationById(
        Long id) {

    ChatConversation chat =
            repository.findById(id)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Conversation not found"));

    return ConversationResponseDto
            .builder()
            .id(chat.getId())
            .userId(
                    chat.getUserId())
            .question(
                    chat.getUserMessage())
            .answer(
                    chat.getAiResponse())
            .module(
                    chat.getModule())
            .createdAt(
                    chat.getCreatedAt())
            .build();
}

@Override
public String deleteConversation(
        Long id) {

    ChatConversation chat =
            repository.findById(id)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Conversation not found"));

    repository.delete(chat);

    return "Conversation deleted successfully";
}

}

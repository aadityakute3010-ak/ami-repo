package com.ami.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.ami.dto.requests.CreateChatRequestDto;
import com.ami.dto.responses.ChatResponseDto;
import com.ami.dto.responses.ConversationResponseDto;
import com.ami.service.AiChatService;

@RestController
@RequestMapping("/api/ai/chat")
public class AiChatController {

    private final AiChatService aiChatService;

    public AiChatController(
            AiChatService aiChatService) {

        this.aiChatService = aiChatService;
    }
    @PreAuthorize(
    	    "hasAnyRole('SUPER_ADMIN','ADMIN','USER')")
    @PostMapping
    public ChatResponseDto askQuestion(
            @RequestBody
            CreateChatRequestDto request) {

        return aiChatService
                .askQuestion(request);
    }
    @PreAuthorize(
    	    "hasAnyRole('SUPER_ADMIN','ADMIN')")
    @GetMapping
    public List<ConversationResponseDto>
    getAllConversations() {

        return aiChatService
                .getAllConversations();
    }
    @PreAuthorize(
    	    "hasAnyRole('SUPER_ADMIN','ADMIN','USER')")
    @GetMapping("/{id}")
    public ConversationResponseDto
    getConversationById(
            @PathVariable Long id) {

        return aiChatService
                .getConversationById(id);
    }
    @PreAuthorize(
    	    "hasAnyRole('SUPER_ADMIN','ADMIN')")

    @DeleteMapping("/{id}")
    public String deleteConversation(
            @PathVariable Long id) {

        return aiChatService
                .deleteConversation(id);
    }
}
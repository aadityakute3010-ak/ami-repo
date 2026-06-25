package com.ami.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.ami.dto.requests.CreatePromptRequestDto;
import com.ami.dto.requests.UpdatePromptRequestDto;
import com.ami.dto.responses.PromptResponseDto;
import com.ami.service.PromptService;

@RestController
@RequestMapping("/api/ai/prompts")
public class PromptController {

    private final PromptService promptService;

    public PromptController(
            PromptService promptService) {

        this.promptService = promptService;
    }
    @PreAuthorize(
    	    "hasAnyRole('SUPER_ADMIN','ADMIN','USER')")
    @PostMapping
    public PromptResponseDto createPrompt(
            @RequestBody
            CreatePromptRequestDto request) {

        return promptService.createPrompt(
                request);
    }
    @PreAuthorize(
    	    "hasAnyRole('SUPER_ADMIN','ADMIN')")
    @GetMapping
    public List<PromptResponseDto>
    getAllPrompts() {

        return promptService
                .getAllPrompts();
    }
    @PreAuthorize(
    	    "hasAnyRole('SUPER_ADMIN','ADMIN')")
    @GetMapping("/{id}")
    public PromptResponseDto
    getPromptById(
            @PathVariable Long id) {

        return promptService
                .getPromptById(id);
    }
    @PreAuthorize(
    	    "hasAnyRole('SUPER_ADMIN','ADMIN')")
    @PutMapping("/{id}")
    public PromptResponseDto
    updatePrompt(
            @PathVariable Long id,
            @RequestBody
            UpdatePromptRequestDto request) {

        return promptService
                .updatePrompt(
                        id,
                        request);
    }
    @PreAuthorize(
    	    "hasAnyRole('SUPER_ADMIN','ADMIN')")
    @DeleteMapping("/{id}")
    public String deletePrompt(
            @PathVariable Long id) {

        return promptService
                .deletePrompt(id);
    }
}
package com.ami.service;

import java.util.List;

import com.ami.dto.requests.CreatePromptRequestDto;
import com.ami.dto.requests.UpdatePromptRequestDto;
import com.ami.dto.responses.PromptResponseDto;

public interface PromptService {

    PromptResponseDto createPrompt(
            CreatePromptRequestDto request);

    List<PromptResponseDto>
    getAllPrompts();

    PromptResponseDto
    getPromptById(Long id);

    PromptResponseDto updatePrompt(
            Long id,
            UpdatePromptRequestDto request);

    String deletePrompt(Long id);
}
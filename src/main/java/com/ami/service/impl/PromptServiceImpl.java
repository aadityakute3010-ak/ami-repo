package com.ami.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ami.dto.requests.CreatePromptRequestDto;
import com.ami.dto.requests.UpdatePromptRequestDto;
import com.ami.dto.responses.PromptResponseDto;
import com.ami.entity.PromptTemplate;
import com.ami.repository.PromptTemplateRepository;
import com.ami.service.PromptService;

@Service
public class PromptServiceImpl
        implements PromptService {

    private final PromptTemplateRepository repository;

    public PromptServiceImpl(
            PromptTemplateRepository repository) {

        this.repository = repository;
    }

    @Override
    public PromptResponseDto createPrompt(
            CreatePromptRequestDto request) {

        PromptTemplate prompt =
                PromptTemplate.builder()
                        .title(request.getTitle())
                        .promptText(
                                request.getPromptText())
                        .module(
                                request.getModule())
                        .active(
                                request.getActive())
                        .build();

        prompt = repository.save(prompt);

        return mapToResponse(prompt);
    }

    @Override
    public List<PromptResponseDto>
    getAllPrompts() {

        return repository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public PromptResponseDto
    getPromptById(Long id) {

        PromptTemplate prompt =
                repository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Prompt not found"));

        return mapToResponse(prompt);
    }

    @Override
    public PromptResponseDto updatePrompt(
            Long id,
            UpdatePromptRequestDto request) {

        PromptTemplate prompt =
                repository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Prompt not found"));

        prompt.setTitle(
                request.getTitle());

        prompt.setPromptText(
                request.getPromptText());

        prompt.setModule(
                request.getModule());

        prompt.setActive(
                request.getActive());

        prompt = repository.save(prompt);

        return mapToResponse(prompt);
    }

    @Override
    public String deletePrompt(
            Long id) {

        PromptTemplate prompt =
                repository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Prompt not found"));

        repository.delete(prompt);

        return "Prompt deleted successfully";
    }

    private PromptResponseDto
    mapToResponse(
            PromptTemplate prompt) {

        return PromptResponseDto
                .builder()
                .id(prompt.getId())
                .title(prompt.getTitle())
                .promptText(
                        prompt.getPromptText())
                .module(prompt.getModule())
                .active(prompt.getActive())
                .build();
    }
}
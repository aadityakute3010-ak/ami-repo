package com.ami.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.ami.dto.requests.UploadKnowledgeRequestDto;
import com.ami.dto.responses.KnowledgeResponseDto;
import com.ami.service.KnowledgeService;

@RestController
@RequestMapping("/api/ai/knowledge")
public class KnowledgeController {

    private final KnowledgeService knowledgeService;

    public KnowledgeController(
            KnowledgeService knowledgeService) {

        this.knowledgeService = knowledgeService;
    }
    @PreAuthorize(
    	    "hasAnyRole('SUPER_ADMIN','ADMIN')")
    @PostMapping("/upload")
    public KnowledgeResponseDto uploadDocument(
            @RequestBody
            UploadKnowledgeRequestDto request) {

        return knowledgeService
                .uploadDocument(request);
    }
    @PreAuthorize(
    	    "hasAnyRole('SUPER_ADMIN','ADMIN','USER')")
    @GetMapping
    public List<KnowledgeResponseDto>
    getAllDocuments() {

        return knowledgeService
                .getAllDocuments();
    }
    @PreAuthorize(
    	    "hasAnyRole('SUPER_ADMIN','ADMIN','USER')")
    @GetMapping("/{id}")
    public KnowledgeResponseDto
    getDocumentById(
            @PathVariable Long id) {

        return knowledgeService
                .getDocumentById(id);
    }
    @PreAuthorize(
    	    "hasAnyRole('SUPER_ADMIN','ADMIN')")
    @DeleteMapping("/{id}")
    public String deleteDocument(
            @PathVariable Long id) {

        return knowledgeService
                .deleteDocument(id);
    }
}
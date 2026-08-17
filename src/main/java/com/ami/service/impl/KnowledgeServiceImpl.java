package com.ami.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ami.dto.requests.UploadKnowledgeRequestDto;
import com.ami.dto.responses.KnowledgeResponseDto;
import com.ami.entity.KnowledgeDocument;
import com.ami.repository.KnowledgeDocumentRepository;
import com.ami.service.KnowledgeService;

@Service
public class KnowledgeServiceImpl
        implements KnowledgeService {

    private final KnowledgeDocumentRepository repository;

    public KnowledgeServiceImpl(
            KnowledgeDocumentRepository repository) {

        this.repository = repository;
    }

    @Override
    public KnowledgeResponseDto uploadDocument(
            UploadKnowledgeRequestDto request) {

        KnowledgeDocument document =
                KnowledgeDocument.builder()
                        .fileName(
                                request.getFileName())
                        .fileType(
                                request.getFileType())
                        .fileSize(
                                request.getFileSize())
                        .uploadedBy(
                                request.getUploadedBy())
                        .description(
                                request.getDescription())
                        .filePath(
                                request.getFilePath())
                        .status("ACTIVE")
                        .indexed(false)
                        .extractedContent("")
                        .build();

        document =
                repository.save(document);

        return mapToResponse(document);
    }

    @Override
    public List<KnowledgeResponseDto>
    getAllDocuments() {

        return repository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public KnowledgeResponseDto
    getDocumentById(Long id) {

        KnowledgeDocument document =
                repository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Document not found"));

        return mapToResponse(document);
    }

    @Override
    public String deleteDocument(
            Long id) {

        KnowledgeDocument document =
                repository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Document not found"));

        repository.delete(document);

        return "Document deleted successfully";
    }

    private KnowledgeResponseDto
    mapToResponse(
            KnowledgeDocument document) {

        return KnowledgeResponseDto
                .builder()
                .id(document.getId())
                .fileName(
                        document.getFileName())
                .fileType(
                        document.getFileType())
                .fileSize(
                        document.getFileSize())
                .uploadedBy(
                        document.getUploadedBy())
                .description(
                        document.getDescription())
                .status(
                        document.getStatus())
                .filePath(
                        document.getFilePath())
                .build();
    }
}
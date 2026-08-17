package com.ami.service;

import java.util.List;

import com.ami.dto.requests.UploadKnowledgeRequestDto;
import com.ami.dto.responses.KnowledgeResponseDto;

public interface KnowledgeService {

    KnowledgeResponseDto uploadDocument(
            UploadKnowledgeRequestDto request);

    List<KnowledgeResponseDto>
    getAllDocuments();

    KnowledgeResponseDto
    getDocumentById(
            Long id);

    String deleteDocument(
            Long id);
}
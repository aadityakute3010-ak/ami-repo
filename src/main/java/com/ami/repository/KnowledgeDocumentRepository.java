package com.ami.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ami.entity.KnowledgeDocument;

@Repository
public interface KnowledgeDocumentRepository
        extends JpaRepository<KnowledgeDocument, Long> {

    List<KnowledgeDocument> findByStatus(
            String status);

    List<KnowledgeDocument> findByIndexed(
            Boolean indexed);

    List<KnowledgeDocument> findByFileType(
            String fileType);
}
package com.ami.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "knowledge_documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KnowledgeDocument extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;

    private String fileType;

    private Long fileSize;

    private String uploadedBy;

    private String description;

    private String status;

    private String filePath;
    
    @Column(columnDefinition = "LONGTEXT")
    private String extractedContent;

    private Boolean indexed;
}
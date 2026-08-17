package com.ami.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ami.entity.IssueAttachment;

public interface IssueAttachmentRepository
        extends JpaRepository<IssueAttachment, Long> {

    List<IssueAttachment> findByIssueId(Long issueId);
}
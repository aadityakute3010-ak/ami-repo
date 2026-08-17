package com.ami.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ami.entity.IssueComment;

public interface IssueCommentRepository
        extends JpaRepository<IssueComment, Long> {

    List<IssueComment> findByIssueIdOrderByCreatedAtDesc(
            Long issueId);
}
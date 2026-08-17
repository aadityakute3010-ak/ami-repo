package com.ami.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ami.entity.IssueRejectionHistory;

public interface IssueRejectionHistoryRepository
        extends JpaRepository<IssueRejectionHistory, Long> {

    List<IssueRejectionHistory> findByIssueIdOrderByRejectedAtDesc(
            Long issueId);
}
package com.ami.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ami.entity.IssueTimeline;

public interface IssueTimelineRepository
        extends JpaRepository<IssueTimeline, Long> {

    List<IssueTimeline> findByIssueIdOrderByCreatedAtDesc(
            Long issueId);
}
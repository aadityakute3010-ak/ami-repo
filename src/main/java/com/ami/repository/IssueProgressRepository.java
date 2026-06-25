package com.ami.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ami.entity.IssueProgress;

@Repository
public interface IssueProgressRepository
        extends JpaRepository<IssueProgress, Long> {

    List<IssueProgress> findByIssueId(
            Long issueId);
}
package com.ami.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ami.entity.IssueEscalation;

@Repository
public interface IssueEscalationRepository
        extends JpaRepository<IssueEscalation, Long> {

    List<IssueEscalation> findByIssueId(
            Long issueId);
}
package com.ami.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ami.entity.FieldVisit;

public interface FieldVisitRepository
        extends JpaRepository<FieldVisit, Long> {

    List<FieldVisit> findByIssueId(Long issueId);
}
package com.ami.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ami.entity.IssueMaterial;

public interface IssueMaterialRepository
        extends JpaRepository<IssueMaterial, Long> {

    List<IssueMaterial> findByIssueId(Long issueId);
}
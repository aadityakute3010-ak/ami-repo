package com.ami.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ami.entity.MaterialUsed;

@Repository
public interface MaterialUsedRepository
        extends JpaRepository<MaterialUsed, Long> {

    List<MaterialUsed> findByIssueId(
            Long issueId);
}

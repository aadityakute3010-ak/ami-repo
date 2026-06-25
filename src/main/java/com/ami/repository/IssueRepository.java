package com.ami.repository;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ami.entity.Issue;
import com.ami.enums.IssueCategory;
import com.ami.enums.IssuePriority;
import com.ami.enums.IssueStatus;
import com.ami.enums.SourceType;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long> {

    Long countByStatus(IssueStatus status);
    
    List<Issue> findByStatus(IssueStatus status);
    
    List<Issue> findByPriority( IssuePriority priority);
    
    List<Issue> findByCategory( IssueCategory category);
    
    List<Issue> findBySourceType( SourceType sourceType);
    
    long countByAssignedEngineerId(
            Long engineerId);

    long countByAssignedEngineerIdAndStatus(
            Long engineerId,
            IssueStatus status);

}
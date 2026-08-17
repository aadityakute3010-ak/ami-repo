package com.ami.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ami.entity.Issue;
import com.ami.enums.EngineerAvailabilityStatus;
import com.ami.enums.IssueCategory;
import com.ami.enums.IssuePriority;
import com.ami.enums.IssueStatus;
import com.ami.enums.RoleType;
import com.ami.enums.SourceType;

@Repository
public interface IssueRepository extends JpaRepository<Issue,Long>, JpaSpecificationExecutor<Issue>{

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
    
    List<Issue> findByAssignedEngineerId(
            Long engineerId);
    
    Optional<Issue> findTopByAssignedEngineerIdOrderByUpdatedAtDesc(
            Long engineerId);
    
    List<Issue> findBySlaBreachedTrue();
    
    List<Issue> findByCreatedAtBetween(
            LocalDateTime start,
            LocalDateTime end);
    
    List<Issue> findByCustomerId(
            Long customerId);
    
    List<Issue> findByMeterId(
            String meterId);
    
    Long countBySlaBreachedTrue();
    
    long countByEscalatedTrue();

    long countByPriority(IssuePriority priority);
    
    long countByCategory(IssueCategory category);
    
    Long countBySlaBreachedFalse();
    
    
    @Query("""
    		SELECT i
    		FROM Issue i
    		WHERE i.assignedEngineer.id = :engineerId
    		AND (:status IS NULL OR i.status = :status)
    		AND (:priority IS NULL OR i.priority = :priority)
    		AND (
    		    :search IS NULL
    		    OR LOWER(i.ticketNumber) LIKE LOWER(CONCAT('%', :search, '%'))
    		    OR LOWER(i.title) LIKE LOWER(CONCAT('%', :search, '%'))
    		    OR LOWER(i.deviceId) LIKE LOWER(CONCAT('%', :search, '%'))
    		    OR LOWER(i.customerName) LIKE LOWER(CONCAT('%', :search, '%'))
    		)
    		""")
    		Page<Issue> findAssignedIssues(
    		        @Param("engineerId") Long engineerId,
    		        @Param("status") IssueStatus status,
    		        @Param("priority") IssuePriority priority,
    		        @Param("search") String search,
    		        Pageable pageable);

}
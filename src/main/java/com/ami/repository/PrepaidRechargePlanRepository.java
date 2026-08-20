package com.ami.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ami.entity.PrepaidRechargePlan;
import com.ami.entity.User;
import com.ami.enums.PrepaidPlanStatus;
import com.ami.enums.SourceType;

public interface PrepaidRechargePlanRepository extends JpaRepository<PrepaidRechargePlan, Long> {

	List<PrepaidRechargePlan> findBySourceTypeAndStatusOrderByAmountAsc(SourceType sourceType,
			PrepaidPlanStatus status);

	boolean existsByAmountAndSourceTypeAndStatus(BigDecimal amount, SourceType sourceType, PrepaidPlanStatus status);

	@Query("""
			SELECT p
			FROM PrepaidRechargePlan p
			WHERE (:sourceType IS NULL OR p.sourceType = :sourceType)
			AND (:status IS NULL OR p.status = :status)
			AND (:search IS NULL OR
			    LOWER(p.planName) LIKE LOWER(CONCAT('%', :search, '%'))
			    OR LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%'))
			)
			ORDER BY p.amount ASC
			""")
	Page<PrepaidRechargePlan> findPlansWithFilters(@Param("search") String search,
			@Param("sourceType") SourceType sourceType, @Param("status") PrepaidPlanStatus status, Pageable pageable);

	List<PrepaidRechargePlan> findBySourceTypeAndStatusAndCreatedByInOrderByAmountAsc(SourceType sourceType,
			PrepaidPlanStatus status, List<User> createdBy);

	@Query("""
			SELECT p
			FROM PrepaidRechargePlan p
			WHERE (:sourceType IS NULL OR p.sourceType = :sourceType)
			AND (:status IS NULL OR p.status = :status)
			AND (:createdBy IS NULL OR p.createdBy = :createdBy)
			AND (:search IS NULL OR
			    LOWER(p.planName) LIKE LOWER(CONCAT('%', :search, '%'))
			    OR LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%'))
			)
			ORDER BY p.amount ASC
			""")
	Page<PrepaidRechargePlan> findPlansWithFiltersAndCreator(@Param("search") String search,
			@Param("sourceType") SourceType sourceType, @Param("status") PrepaidPlanStatus status,
			@Param("createdBy") User createdBy, Pageable pageable);

	@Query("""
			SELECT p
			FROM PrepaidRechargePlan p
			WHERE p.sourceType = :sourceType
			AND p.status = :status
			AND (
			    p.createdBy.role = com.ami.enums.RoleType.SUPER_ADMIN
			    OR p.createdBy = :admin
			)
			ORDER BY p.amount ASC
			""")
	List<PrepaidRechargePlan> findActivePlansForAdminOrUserDevice(@Param("sourceType") SourceType sourceType,
			@Param("status") PrepaidPlanStatus status, @Param("admin") User admin);

}
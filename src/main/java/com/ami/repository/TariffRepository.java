package com.ami.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ami.entity.Tariff;
import com.ami.entity.User;
import com.ami.enums.RoleType;
import com.ami.enums.SourceType;
import com.ami.enums.TariffCategory;
import com.ami.enums.TariffStatus;

@Repository
public interface TariffRepository extends JpaRepository<Tariff, Long> {

	List<Tariff> findAllByOrderByCreatedAtDesc();

	List<Tariff> findByStatusOrderByCreatedAtDesc(TariffStatus status);

	List<Tariff> findBySourceAndStatusOrderByCreatedAtDesc(SourceType source, TariffStatus status);

	boolean existsByNameIgnoreCaseAndSourceAndCategory(String name, SourceType source, TariffCategory category);

	boolean existsByNameIgnoreCaseAndSourceAndCategoryAndIdNot(String name, SourceType source, TariffCategory category,
			Long id);

	List<Tariff> findBySourceAndStatusOrderByNameAsc(SourceType source, TariffStatus status);

	@Query("""
			SELECT t
			FROM Tariff t
			WHERE (:source IS NULL OR t.source = :source)
			AND (:category IS NULL OR t.category = :category)
			AND (:status IS NULL OR t.status = :status)
			AND (
			    :search IS NULL
			    OR LOWER(t.name) LIKE LOWER(CONCAT('%', :search, '%'))
			    OR LOWER(t.unit) LIKE LOWER(CONCAT('%', :search, '%'))
			    OR LOWER(t.description) LIKE LOWER(CONCAT('%', :search, '%'))
			)
			ORDER BY t.createdAt DESC
			""")
	List<Tariff> findWithFilters(@Param("source") SourceType source, @Param("category") TariffCategory category,
			@Param("status") TariffStatus status, @Param("search") String search);

	boolean existsByNameIgnoreCaseAndSourceAndCategoryAndStatus(String name, SourceType source, TariffCategory category,
			TariffStatus status);

	boolean existsByNameIgnoreCaseAndSourceAndCategoryAndStatusAndIdNot(String name, SourceType source,
			TariffCategory category, TariffStatus status, Long id);

	boolean existsByCreatedByAndNameIgnoreCaseAndSourceAndCategoryAndStatus(User createdBy, String name,
			SourceType source, TariffCategory category, TariffStatus status);

	boolean existsByCreatedByAndNameIgnoreCaseAndSourceAndCategoryAndStatusAndIdNot(User createdBy, String name,
			SourceType source, TariffCategory category, TariffStatus status, Long id);

	Optional<Tariff> findFirstByCreatedByAndSourceAndStatusOrderByCreatedAtDesc(User createdBy, SourceType source,
			TariffStatus status);

	Optional<Tariff> findFirstByCreatedByAndSourceAndCategoryAndStatusOrderByCreatedAtDesc(User createdBy,
			SourceType source, TariffCategory category, TariffStatus status);

	Optional<Tariff> findFirstByCreatedBy_RoleAndSourceAndCategoryAndStatusOrderByCreatedAtDesc(RoleType role,
			SourceType source, TariffCategory category, TariffStatus status);

}
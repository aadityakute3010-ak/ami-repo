package com.ami.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ami.entity.TariffSlab; 
import com.ami.enums.TariffStatus;

@Repository
public interface TariffSlabRepository extends JpaRepository<TariffSlab, Long> {

	List<TariffSlab> findByTariff_IdOrderByFromUnitAsc(Long tariffId);

	List<TariffSlab> findByTariff_IdAndStatusOrderByFromUnitAsc(Long tariffId, TariffStatus status);

	boolean existsByTariff_IdAndFromUnit(Long tariffId, BigDecimal fromUnit);

	boolean existsByTariff_IdAndFromUnitAndIdNot(Long tariffId, BigDecimal fromUnit, Long slabId);

	@Query("""
			SELECT CASE
				WHEN COUNT(s) > 0
				THEN true
				ELSE false
			END
			FROM TariffSlab s
			WHERE s.tariff.id = :tariffId
			AND (
				:excludeSlabId IS NULL
				OR s.id <> :excludeSlabId
			)
			AND (
				(
					:toUnit IS NULL
					AND (
						s.toUnit IS NULL
						OR s.toUnit > :fromUnit
					)
				)
				OR
				(
					:toUnit IS NOT NULL
					AND s.fromUnit < :toUnit
					AND (
						s.toUnit IS NULL
						OR s.toUnit > :fromUnit
					)
				)
			)
			""")
	boolean existsOverlappingSlab(@Param("tariffId") Long tariffId,
			                      @Param("fromUnit") BigDecimal fromUnit,
                                  @Param("toUnit") BigDecimal toUnit,
                                  @Param("excludeSlabId") Long excludeSlabId);
}
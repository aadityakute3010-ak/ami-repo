package com.ami.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.ami.enums.TariffStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tariff_slabs", uniqueConstraints = {
		@UniqueConstraint(name = "uk_tariff_slab_from_unit", columnNames = { "tariff_id", "from_unit" }) })
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TariffSlab {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "tariff_id", nullable = false)
	private Tariff tariff;

	@Column(name = "from_unit", nullable = false, precision = 19, scale = 4)
	private BigDecimal fromUnit;

	@Column(name = "to_unit", precision = 19, scale = 4)
	private BigDecimal toUnit;

	@Column(name = "rate_per_unit", nullable = false, precision = 19, scale = 4)
	private BigDecimal rate;

	@Column(name = "fixed_charge", nullable = false, precision = 19, scale = 2)
	private BigDecimal fixedCharge;

	@Column(name = "tax_percentage", nullable = false, precision = 7, scale = 4)
	private BigDecimal tax;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private TariffStatus status;

	@Column(length = 1000)
	private String description;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@PrePersist
	private void prePersist() {

		LocalDateTime now = LocalDateTime.now();

		createdAt = now;
		updatedAt = now;
	}

	@PreUpdate
	private void preUpdate() {
		updatedAt = LocalDateTime.now();
	}
}
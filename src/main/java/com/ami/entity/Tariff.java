package com.ami.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.ami.enums.SourceType;
import com.ami.enums.TariffCategory;
import com.ami.enums.TariffStatus;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tariffs")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tariff {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "tariff_name", nullable = false, length = 150)
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private SourceType source;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private TariffCategory category;

	@Column(nullable = false, length = 30)
	private String unit;

	@Column(name = "base_rate", nullable = false, precision = 19, scale = 4)
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "created_by", nullable = false)
	private User createdBy;

	@OneToMany(mappedBy = "tariff", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("fromUnit ASC")
	@Builder.Default
	private List<TariffSlab> slabs = new ArrayList<>();

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@Version
	private Long version;

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

	public void addSlab(TariffSlab slab) {

		slabs.add(slab);
		slab.setTariff(this);
	}

	public void removeSlab(TariffSlab slab) {

		slabs.remove(slab);
		slab.setTariff(null);
	}
}
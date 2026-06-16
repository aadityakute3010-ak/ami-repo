package com.ami.entity;

import java.time.LocalDateTime;
import com.ami.enums.BillingType;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "devices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class Device extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// Device Identity

	@Column(unique = true, nullable = false)
	private String deviceId;

	@Column(unique = true, nullable = false)
	private String macAddress;

	@Column(unique = true, nullable = false)
	private String serialNumber;

	@Column(nullable = false)
	private String deviceName;

	@Enumerated(EnumType.STRING)
	private BillingType billingType;

	// Customer Information

	private String customerName;

	private String customerAddress;

	private String buildingOrWing;

	private String area;

	private String zone;

	private String city;

	private String state;

	private String meterLocation;

	// Runtime Information

	private Boolean active;

	private Boolean online;

	private LocalDateTime lastSyncTime;

	// Ownership

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "created_by_id")
	private User createdBy;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "assigned_admin_id")
	private User assignedAdmin;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "assigned_user_id")
	private User assignedUser;

	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "meter_id")
	private Meter meter; 
}
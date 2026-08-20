package com.ami.entity.telemetry;

import java.time.LocalDateTime;

import com.ami.entity.Device;
import com.ami.entity.Payload;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "energy_telemetry")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnergyTelemetry {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "device_id")
	private Device device;

	private Double voltage;

	private Double current;

	private Double power;

	private Double frequency;

	private Double powerFactor;

	private Double energyConsumed;

	private Double activePower;

	private Double reactivePower;

	private Double apparentPower;

	@Column(name = "load_value")
	private Double load; 

	private Double demand;

	private Double batteryLevel;

	private Double signalStrength;

	private LocalDateTime readingTime;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "payload_id", unique = true)
	private Payload payload;
}
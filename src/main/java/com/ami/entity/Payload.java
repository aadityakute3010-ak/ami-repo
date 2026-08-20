package com.ami.entity;

import java.time.LocalDateTime;

import com.ami.enums.PayloadStatus;
import com.ami.enums.SensorStatus;
import com.ami.enums.ValveStatus;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "payloads", indexes = { @Index(name = "idx_payload_device", columnList = "device_id"),
		@Index(name = "idx_payload_received_at", columnList = "received_at"),
		@Index(name = "idx_payload_status", columnList = "status") })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class Payload extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	//Device Relation
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "device_id", nullable = false)
	private Device device;

	//Meter Readings
	@Column(nullable = false)
	private Double startReading;

	@Column(nullable = false)
	private Double endReading;

	private Double consumption;

	private Double startBalance;

	private Double endBalance;

	//Communication Data
	private Integer batteryPercentage;

	private Integer signalQuality;

	private Integer signalPower;

	private Integer snr;

	//Device Snapshot Information
	@Column(length = 50)
	private String firmwareVersion;

	@Column(length = 50)
	private String simNumber;

	@Column(length = 100)
	private String consumerNumber;

	// Payload Status
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private PayloadStatus status;

	@Column(length = 500)
	private String failureReason;

	//Timeline Information
	@Column(nullable = false)
	private LocalDateTime receivedAt;

	@Enumerated(EnumType.STRING)
	private ValveStatus valveStatus;

	@Enumerated(EnumType.STRING)
	private SensorStatus sensorStatus;

	//Raw Payload JSON
	@Lob
	@Column(columnDefinition = "LONGTEXT")
	private String rawPayload;
}
package com.ami.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "daily_consumption", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "device_id", "reading_date" }) })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyConsumption {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// one device can have many daily summaries
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "device_id", nullable = false)
	private Device device;

	@Column(name = "reading_date", nullable = false)
	private LocalDate readingDate;

	@Column(name = "opening_reading")
	private Double openingReading;

	@Column(name = "closing_reading")
	private Double closingReading;

	@Column(name = "total_reading")
	private Double totalReading;

	@Column(name = "daily_consumption")
	private Double dailyConsumption;

	@Column(name = "first_payload_time")
	private LocalDateTime firstPayloadTime;

	@Column(name = "last_payload_time")
	private LocalDateTime lastPayloadTime;

	@Column(name = "success_payload_count")
	private Long successPayloadCount;

	@Column(name = "failed_payload_count")
	private Long failedPayloadCount;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;
}
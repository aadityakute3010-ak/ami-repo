package com.ami.entity.telemetry;

import java.time.LocalDateTime;
import com.ami.entity.Device;
import com.ami.entity.Payload;

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
@Table(name = "gas_telemetry")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GasTelemetry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id")
    private Device device;

    private Double gasFlow;

    private Double gasPressure;
    
    private Double gasVolume;
    
	private Double temperature;

	private String pipelineHealth;

    private Double totalConsumption;

    private Double batteryLevel;

    private Double signalStrength;

    private LocalDateTime readingTime;
    
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "payload_id", unique = true)
	private Payload payload; 
}
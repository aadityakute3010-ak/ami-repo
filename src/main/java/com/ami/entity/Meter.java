package com.ami.entity;

import com.ami.enums.DeviceStatus;
import com.ami.enums.SourceType;
import com.ami.enums.TechnologyType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "meters")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Meter extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =====================================================
    // COMMON METER INFORMATION
    // =====================================================

    @NotBlank(message = "Meter name is required")
    private String meterName;

    @NotNull(message = "Source type is required")
    @Enumerated(EnumType.STRING)
    private SourceType sourceType;

    @NotNull(message = "Technology type is required")
    @Enumerated(EnumType.STRING)
    private TechnologyType technologyType;

    @Enumerated(EnumType.STRING)
    private DeviceStatus status;

    // =====================================================
    // COMMON DYNAMIC FIELDS
    // =====================================================

    private String meterType;

    private String application;

    // =====================================================
    // WATER
    // =====================================================

    private String diameterSize;

    @PositiveOrZero(message = "Liter per pulse cannot be negative")
    private Double literPerPulse;

    // =====================================================
    // ENERGY
    // =====================================================

    private String ctRatio;

    private String ptRatio;

    private String voltageClass;

    // =====================================================
    // SOLAR
    // =====================================================

    private String inverterType;

    private String plantCapacity;

    @PositiveOrZero(message = "Panel count cannot be negative")
    private Integer panelCount;

    // =====================================================
    // COMMON
    // =====================================================

    @PositiveOrZero(message = "Meter start reading cannot be negative")
    private Double meterStartReading;

    // =====================================================
    // RELATION
    // =====================================================

    @JsonIgnore
    @OneToOne(mappedBy = "meter")
    private Device device;
}
package com.ami.entity;

import com.ami.enums.AmiApplicationType;
import com.ami.enums.ApplicationOfAmi;
import com.ami.enums.DeviceStatus;
import com.ami.enums.DiameterSize;
import com.ami.enums.SourceType;
import com.ami.enums.TechnologyType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnore;

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

    private String meterName;

    @Enumerated(EnumType.STRING)
    private SourceType sourceType;

    @Enumerated(EnumType.STRING)
    private TechnologyType technologyType;

    @Enumerated(EnumType.STRING)
    private DeviceStatus status;

    @Enumerated(EnumType.STRING)
    private ApplicationOfAmi applicationOfAmi;

    @Enumerated(EnumType.STRING)
    private AmiApplicationType amiApplicationType;

    @Enumerated(EnumType.STRING)
    private DiameterSize diameterSize;

    private Double literPerPulse;

    private Double meterStartReading;
    
    @JsonIgnore 
    @OneToOne(mappedBy = "meter")
    private Device device; 
    
} 
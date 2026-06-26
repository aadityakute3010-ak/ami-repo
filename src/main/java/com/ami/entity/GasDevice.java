package com.ami.entity;

import com.ami.enums.DeviceStatus;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "gas_devices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GasDevice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String deviceId;

    private String deviceName;

    private String serialNumber;

    private String deviceType;

    private String manufacturer;

    private String firmwareVersion;
    
    private String latestFirmwareVersion;

    private String firmwareStatus;

    private java.time.LocalDateTime firmwareUpdatedAt;

    private String location;

    private String zoneName;

    private Double latitude;

    private Double longitude;

    @Enumerated(EnumType.STRING)
    private DeviceStatus status;

    private Boolean active;
    
    
}
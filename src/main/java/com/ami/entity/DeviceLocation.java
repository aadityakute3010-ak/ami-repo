package com.ami.entity;

import com.ami.enums.DeviceLocationSource;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "device_locations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceLocation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false, unique = true)
    private Device device;

    private String address;

    private String city;

    private String state;

    private String country;

    private Double latitude;

    private Double longitude;

    @Enumerated(EnumType.STRING)
    @Column(name = "location_source")
    private DeviceLocationSource locationSource;
}
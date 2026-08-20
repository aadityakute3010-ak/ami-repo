package com.ami.dto.responses;

import com.ami.enums.DeviceLocationSource;
import com.ami.enums.DeviceStatus;
import com.ami.enums.SourceType;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceMapMarkerDto {

    private Long deviceIdPk;

    private String deviceId;

    private String deviceName;

    private String customerName;

    private String address;

    private String city;

    private String state;

    private String country;

    private Double latitude;

    private Double longitude;

    private SourceType sourceType;

    private DeviceStatus status;

    private Boolean online;

    private DeviceLocationSource locationSource;
}
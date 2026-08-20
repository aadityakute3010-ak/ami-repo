package com.ami.dto.responses;

import com.ami.enums.RoleType;
import com.ami.enums.StatusType;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserMapMarkerDto {

    private Long userId;

    private String fullName;

    private String email;

    private String phoneNo;

    private String address;

    private String city;

    private String state;

    private String country;

    private Double latitude;

    private Double longitude;

    private RoleType role;

    private StatusType status;
}
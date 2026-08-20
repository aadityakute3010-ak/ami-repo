package com.ami.dto.responses;

import com.ami.enums.RoleType;
import com.ami.enums.SourceType;
import com.ami.enums.StatusType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@RequiredArgsConstructor
public class AdminUpdateUserResponseDto {

    private String firstName;

    private String lastName;

    private String userName;

    private String email;

    private String phoneNo;

    private String address;

    private String state;

    private String city;

    private RoleType role;

    private StatusType status;

    private Set<SourceType> assignedSources;
    
    private Long adminId;
    
    private String adminName;

    }
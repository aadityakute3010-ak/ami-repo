package com.ami.dto.responses; 

import com.ami.enums.RoleType;
import com.ami.enums.SourceType;
import com.ami.enums.StatusType;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@Getter
@Setter
public class UserDetailsResponseDto {

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
    
    //ASSIGNED DEVICES
    private List<String> assignedDevices; 
    
    private List<String> assignedMeters;

    //ACTIVITY DETAILS
    private String createdBy;

    private LocalDateTime createdAt;

    public UserDetailsResponseDto() {
    }
    
}
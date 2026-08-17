package com.ami.dto.requests;

import lombok.Data;

@Data 
public class CustomerInfoDto {

    private String customerName;

    private String customerAddress;

    private String buildingOrWing;

    private String area;

    private String zone;

    private String city;

    private String state;

    private String meterLocation;
}
 
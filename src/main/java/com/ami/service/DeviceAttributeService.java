package com.ami.service;

import java.util.List;

import com.ami.dto.requests.CreateAttributeKeyRequestDto;
import com.ami.dto.requests.CreateDeviceAttributeRequestDto;
import com.ami.dto.responses.DeviceAttributeResponseDto;
import com.ami.entity.AttributeKey;

public interface DeviceAttributeService {

    //Attribute Key (Dropdown)
    List<AttributeKey> getActiveAttributeKeys();

    //Device Attribute
    DeviceAttributeResponseDto createAttribute(Long deviceId, CreateDeviceAttributeRequestDto request);

    List<DeviceAttributeResponseDto> getDeviceAttributes(Long deviceId);
    
    AttributeKey createAttributeKey(CreateAttributeKeyRequestDto request); 
    
    AttributeKey updateAttributeKey(Long keyId, CreateAttributeKeyRequestDto request);

    void deleteAttributeKey(Long keyId);
}
 
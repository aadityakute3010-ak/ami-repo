package com.ami.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ami.entity.DeviceAttribute;

@Repository
public interface DeviceAttributeRepository extends JpaRepository<DeviceAttribute, Long> {

    List<DeviceAttribute> findByDeviceId(Long deviceId);

    boolean existsByDeviceIdAndAttributeKeyId(Long deviceId, Long attributeKeyId);
    
    boolean existsByAttributeKeyId(Long attributeKeyId);
    
    List<DeviceAttribute> findByDeviceId(String deviceId); 
} 
package com.ami.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ami.entity.GasDevice;

@Repository
public interface GasDeviceRepository
        extends JpaRepository<GasDevice, Long> {

    Optional<GasDevice>
    findByDeviceId(
            String deviceId);
}
package com.ami.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ami.entity.FirmwareHistory;

@Repository
public interface FirmwareHistoryRepository
        extends JpaRepository<FirmwareHistory, Long> {

    List<FirmwareHistory> findByDeviceIdOrderByUpdatedAtDesc(
            String deviceId);
}
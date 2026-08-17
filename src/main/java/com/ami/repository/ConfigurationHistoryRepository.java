package com.ami.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ami.entity.ConfigurationHistory;

@Repository
public interface ConfigurationHistoryRepository
        extends JpaRepository<ConfigurationHistory, Long> {

    List<ConfigurationHistory> findByConfigurationIdOrderByUpdatedAtDesc(
            Long configurationId);
}
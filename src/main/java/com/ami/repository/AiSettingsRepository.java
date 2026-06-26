package com.ami.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ami.entity.AiSettings;

@Repository
public interface AiSettingsRepository
        extends JpaRepository<AiSettings, Long> {

    Optional<AiSettings>
    findTopByOrderByIdAsc();
}
package com.ami.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ami.entity.InstallationHistory;

@Repository
public interface InstallationHistoryRepository extends JpaRepository<InstallationHistory, Long> {

    List<InstallationHistory> findByInstallationIdOrderByCreatedAtDesc(Long installationId);

}
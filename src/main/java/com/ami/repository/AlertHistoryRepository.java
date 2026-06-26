package com.ami.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ami.entity.AlertHistory;

@Repository
public interface AlertHistoryRepository
        extends JpaRepository<AlertHistory, Long> {

    List<AlertHistory> findByAlertId(
            Long alertId);
}
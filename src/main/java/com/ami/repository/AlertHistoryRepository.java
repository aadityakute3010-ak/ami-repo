package com.ami.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.ami.entity.AlertHistory;

@Repository
public interface AlertHistoryRepository
extends JpaRepository<AlertHistory, Long>,
        JpaSpecificationExecutor<AlertHistory> {

    List<AlertHistory> findByAlertId(
            Long alertId);
}
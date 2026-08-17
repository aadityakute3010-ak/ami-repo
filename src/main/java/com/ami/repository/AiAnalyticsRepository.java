package com.ami.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ami.entity.AiAnalytics;

@Repository
public interface AiAnalyticsRepository
        extends JpaRepository<AiAnalytics, Long> {

    List<AiAnalytics> findByModule(
            String module);

    List<AiAnalytics> findByUserId(
            String userId);
}
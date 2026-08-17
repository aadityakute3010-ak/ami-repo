package com.ami.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ami.entity.InstallationTimeline;

@Repository
public interface InstallationTimelineRepository extends JpaRepository<InstallationTimeline, Long> {

    List<InstallationTimeline> findByInstallationIdOrderByEventTimeAsc(Long installationId);

}
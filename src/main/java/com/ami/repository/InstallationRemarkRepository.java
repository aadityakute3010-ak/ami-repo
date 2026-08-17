package com.ami.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ami.entity.InstallationRemark;

@Repository
public interface InstallationRemarkRepository extends JpaRepository<InstallationRemark, Long> {

    List<InstallationRemark> findByInstallationIdOrderByCreatedAtDesc(Long installationId);

}
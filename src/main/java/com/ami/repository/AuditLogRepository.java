package com.ami.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.ami.entity.AuditLog;

@Repository
public interface AuditLogRepository
extends JpaRepository<AuditLog, Long>, JpaSpecificationExecutor<AuditLog> {

	List<AuditLog> findByModule(String module);

	List<AuditLog> findByEntityId(Long entityId);
} 
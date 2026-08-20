package com.ami.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ami.entity.BillingSettings;
import com.ami.entity.User;

public interface BillingSettingsRepository extends JpaRepository<BillingSettings, Long> {

	Optional<BillingSettings> findByAdmin(User admin);

	Optional<BillingSettings> findByAdminIsNull();
}
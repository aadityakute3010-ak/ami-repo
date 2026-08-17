package com.ami.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ami.entity.InstallationChecklist;

@Repository
public interface InstallationChecklistRepository extends JpaRepository<InstallationChecklist, Long> {

}
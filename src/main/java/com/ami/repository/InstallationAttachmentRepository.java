package com.ami.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ami.entity.InstallationAttachment;

@Repository
public interface InstallationAttachmentRepository
        extends JpaRepository<InstallationAttachment, Long> {

    List<InstallationAttachment> findByInstallationIdOrderByUploadedAtDesc(
            Long installationId);

}
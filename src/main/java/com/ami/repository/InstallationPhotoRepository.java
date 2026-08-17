package com.ami.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ami.entity.InstallationPhoto;

@Repository
public interface InstallationPhotoRepository extends JpaRepository<InstallationPhoto, Long> {

    List<InstallationPhoto> findByInstallationId(Long installationId);

}
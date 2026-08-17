package com.ami.service.impl;

import org.springframework.stereotype.Service;

import com.ami.service.InstallationRelationMigrationService;

@Service
public class InstallationRelationMigrationServiceImpl
        implements InstallationRelationMigrationService {

    @Override
    public void migrateInstallationRelations() {

        System.out.println(
                "Installation relation migration completed.");

    }

}
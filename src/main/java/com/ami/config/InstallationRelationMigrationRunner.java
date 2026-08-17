package com.ami.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.ami.service.InstallationRelationMigrationService;

@Component
public class InstallationRelationMigrationRunner
        implements CommandLineRunner {

    private final InstallationRelationMigrationService
            migrationService;

    public InstallationRelationMigrationRunner(
            InstallationRelationMigrationService migrationService) {

        this.migrationService = migrationService;
    }

    @Override
    public void run(String... args)
            throws Exception {

        // Uncomment only when migration is required.

        // migrationService.migrateInstallationRelations();

    }

}
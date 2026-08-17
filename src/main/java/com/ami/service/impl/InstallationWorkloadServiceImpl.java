package com.ami.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ami.entity.User;
import com.ami.enums.InstallationStatus;
import com.ami.repository.InstallationRepository;
import com.ami.service.InstallationWorkloadService;

@Service
public class InstallationWorkloadServiceImpl
        implements InstallationWorkloadService {

    private final InstallationRepository
            installationRepository;

    public InstallationWorkloadServiceImpl(
            InstallationRepository installationRepository) {

        this.installationRepository =
                installationRepository;
    }
    @Override
    public User getBestEngineer(
            List<User> engineers) {

        return engineers.stream()

                .max((e1, e2) ->

                        calculateEngineerScore(e1)

                                .compareTo(

                                        calculateEngineerScore(e2)))

                .orElse(null);
    }
    @Override
    public Integer calculateEngineerScore(
            User engineer) {

        int score = 0;

        /*
         * Attendance
         */

        score += 20;

        /*
         * Availability
         */

        score += 30;

        /*
         * Less workload = higher score
         */

        long activeJobs =

                installationRepository
                        .countByAssignedEngineerIdAndStatus(
                                engineer.getId(),
                                InstallationStatus.ASSIGNED)

                +

                installationRepository
                        .countByAssignedEngineerIdAndStatus(
                                engineer.getId(),
                                InstallationStatus.IN_PROGRESS);

        score += Math.max(
                0,
                100 - ((int) activeJobs * 10));

        return score;
    }

}
package com.ami.service;

import java.util.List;

import com.ami.entity.User;

public interface InstallationWorkloadService {

    User getBestEngineer(
            List<User> engineers);

    Integer calculateEngineerScore(
            User engineer);

}
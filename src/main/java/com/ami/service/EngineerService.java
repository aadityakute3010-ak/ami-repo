package com.ami.service;

import com.ami.dto.responses.EngineerDashboardResponseDto;
import com.ami.dto.responses.EngineerWorkloadResponseDto;
import com.ami.entity.User;

import java.util.List;

public interface EngineerService {

    List<User> getEngineers();

    User getEngineerById(Long engineerId);

    List<User> getAvailableEngineers();

    EngineerWorkloadResponseDto getWorkload(
            Long engineerId);
    
    EngineerDashboardResponseDto
    getDashboard(
            Long engineerId);
    
}
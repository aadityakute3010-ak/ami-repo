package com.ami.service.impl;

import com.ami.dto.responses.EngineerDashboardResponseDto;
import com.ami.dto.responses.EngineerWorkloadResponseDto;
import com.ami.entity.Issue;
import com.ami.entity.User;
import com.ami.enums.IssueStatus;
import com.ami.repository.IssueRepository;
import com.ami.repository.UserRepository;
import com.ami.service.EngineerService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EngineerServiceImpl
        implements EngineerService {

    private final UserRepository userRepository;
    private final IssueRepository issueRepository;

    public EngineerServiceImpl(
            UserRepository userRepository,
            IssueRepository issueRepository) {

        this.userRepository = userRepository;
        this.issueRepository = issueRepository;
    }

    @Override
    public List<User> getEngineers() {

        return userRepository.findAll();
    }

    @Override
    public User getEngineerById(
            Long engineerId) {

        return userRepository.findById(engineerId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Engineer not found"));
    }

    @Override
    public List<User> getAvailableEngineers() {

        return userRepository.findAll();
    }

    @Override
    public EngineerWorkloadResponseDto getWorkload(
            Long engineerId) {

        EngineerWorkloadResponseDto dto =
                new EngineerWorkloadResponseDto();

        dto.setActiveIssues(
                issueRepository
                        .countByAssignedEngineerIdAndStatus(
                                engineerId,
                                IssueStatus.IN_PROGRESS));

        dto.setResolvedIssues(
                issueRepository
                        .countByAssignedEngineerIdAndStatus(
                                engineerId,
                                IssueStatus.RESOLVED));

        dto.setRejectedIssues(
                issueRepository
                        .countByAssignedEngineerIdAndStatus(
                                engineerId,
                                IssueStatus.REJECTED));

        return dto;
    }
    
    @Override
    public EngineerDashboardResponseDto
    getDashboard(
            Long engineerId) {

        EngineerDashboardResponseDto dto =
                new EngineerDashboardResponseDto();

        dto.setAssigned(
                issueRepository
                        .countByAssignedEngineerIdAndStatus(
                                engineerId,
                                IssueStatus.AUTO_ASSIGNED));

        dto.setInProgress(
                issueRepository
                        .countByAssignedEngineerIdAndStatus(
                                engineerId,
                                IssueStatus.IN_PROGRESS));

        dto.setResolved(
                issueRepository
                        .countByAssignedEngineerIdAndStatus(
                                engineerId,
                                IssueStatus.RESOLVED));

        dto.setEscalated(
                issueRepository
                        .countByAssignedEngineerIdAndStatus(
                                engineerId,
                                IssueStatus.ESCALATED));

        return dto;
    }
   
}
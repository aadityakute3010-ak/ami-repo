package com.ami.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ami.entity.EngineerLeave;
import com.ami.enums.LeaveStatus;

@Repository
public interface EngineerLeaveRepository
        extends JpaRepository<EngineerLeave, Long> {

    List<EngineerLeave> findByEngineerId(
            Long engineerId);

    List<EngineerLeave> findByEngineerIdAndStatus(
            Long engineerId,
            LeaveStatus status);
}
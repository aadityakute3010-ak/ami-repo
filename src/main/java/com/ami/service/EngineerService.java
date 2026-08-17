package com.ami.service;

import com.ami.dto.requests.ApplyLeaveRequestDto;
import com.ami.dto.responses.EngineerActivityResponseDto;
import com.ami.dto.responses.EngineerDashboardResponseDto;
import com.ami.dto.responses.EngineerLeaveResponseDto;
import com.ami.dto.responses.EngineerOperationResponseDto;
import com.ami.dto.responses.EngineerOperationsSummaryResponseDto;
import com.ami.dto.responses.EngineerPerformanceResponseDto;
import com.ami.dto.responses.EngineerStatisticsResponseDto;
import com.ami.dto.responses.EngineerWorkloadResponseDto;
import com.ami.dto.responses.IssueResponseDto;
import com.ami.entity.User;
import com.ami.enums.EngineerAttendanceStatus;
import com.ami.enums.EngineerAvailabilityStatus;
import com.ami.enums.LeaveStatus;

import java.util.List;

import org.springframework.data.domain.Page;

public interface EngineerService {

    List<User> getEngineers();

    User getEngineerById(Long engineerId);

    List<User> getAvailableEngineers();

    EngineerWorkloadResponseDto getWorkload(
            Long engineerId);
    
    EngineerDashboardResponseDto
    getDashboard(
            Long engineerId);
    
    EngineerPerformanceResponseDto getPerformance(
            Long engineerId);

    List<IssueResponseDto> getHistory(
            Long engineerId);

    List<IssueResponseDto> getSchedule(
            Long engineerId);
    

    String updateAttendance(
            Long engineerId,
            EngineerAttendanceStatus status);

    String updateAvailability(
            Long engineerId,
            EngineerAvailabilityStatus status);

    String applyLeave(
            Long engineerId,
            ApplyLeaveRequestDto request);

    List<EngineerLeaveResponseDto> getLeaveHistory(
            Long engineerId);

    Integer getLeaveBalance(
            Long engineerId);

     String updateLeaveStatus(
            Long leaveId,
            LeaveStatus status);
     
     EngineerOperationsSummaryResponseDto
     getOperationsSummary();
     
     List<EngineerActivityResponseDto>
     getActivity(
             Long engineerId);
     
     EngineerStatisticsResponseDto
     getStatistics();
     
     Page<EngineerOperationResponseDto> getEngineerOperations(
    	        String search,
    	        EngineerAttendanceStatus attendanceStatus,
    	        EngineerAvailabilityStatus availabilityStatus,
    	        int page,
    	        int size,
    	        String sortBy,
    	        String sortDirection);
     
     byte[] exportEngineersCsv();

     byte[] exportEngineersExcel();

     byte[] exportEngineersPdf();
     
     byte[] exportEngineer(
    	        Long engineerId,
    	        String format);
    
}
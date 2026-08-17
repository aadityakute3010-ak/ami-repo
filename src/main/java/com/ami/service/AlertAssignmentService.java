package com.ami.service;

import java.util.List;

import com.ami.dto.requests.AssignAdminAlertsRequestDto;
import com.ami.dto.requests.AssignDeviceAlertsRequestDto;
import com.ami.dto.requests.BulkAssignAlertRequestDto;
import com.ami.dto.requests.BulkAssignAlertsRequestDto;
import com.ami.dto.requests.BulkAssignDeviceAlertsRequestDto;
import com.ami.dto.responses.AdminAlertAssignmentPageResponseDto;
import com.ami.dto.responses.AlertAssignmentOverviewResponseDto;
import com.ami.dto.responses.AlertAssignmentResponseDto;
import com.ami.dto.responses.AlertRuleAssignmentPageResponseDto;
import com.ami.dto.responses.AlertRuleAssignmentResponseDto;
import com.ami.dto.responses.DeviceAlertAssignmentPageResponseDto;

public interface AlertAssignmentService {

   
    List<AlertAssignmentResponseDto> assignAlertsToAdmins(
            AssignAdminAlertsRequestDto request);

    
    List<AlertAssignmentResponseDto> bulkAssignAlertsToAdmins(
            BulkAssignAlertsRequestDto request);

   
    List<AlertAssignmentResponseDto> getAssignmentsByAdmin(
            Long adminId);

   
    List<AlertAssignmentResponseDto> getAssignmentsByAlert(
            Long alertId);

  
    String removeAdminFromAlert(
            Long alertId,
            Long adminId);

   
    AlertAssignmentResponseDto enableAssignment(
            Long assignmentId);

    AlertAssignmentResponseDto disableAssignment(
            Long assignmentId);

    boolean isAlertAssignedToAdmin(
            Long alertId,
            Long adminId);

   
    AdminAlertAssignmentPageResponseDto getAssignmentOverview(
            int page,
            int size,
            String search,
            String sortBy,
            String sortDirection);
    
    AlertAssignmentOverviewResponseDto getCompleteAssignmentOverview(
            int page,
            int size,
            String search,
            String sortBy,
            String sortDirection);

  
    AlertRuleAssignmentResponseDto getAlertRuleAssignment(
            Long alertId);
    
   
    AlertRuleAssignmentPageResponseDto getAlertRuleAssignments(
            int page,
            int size,
            String search,
            String sortBy,
            String sortDirection);

    AlertAssignmentResponseDto assignAlert(
            Long alertId,
            com.ami.dto.requests.AssignAlertRequestDto request);

    String unassignAlert(
            Long assignmentId);

    AlertAssignmentResponseDto reassignAlert(
            Long assignmentId,
            com.ami.dto.requests.AssignAlertRequestDto request);

    List<AlertAssignmentResponseDto> getAssignmentHistory(
            Long alertId);

    List<AlertAssignmentResponseDto> getAllActiveAssignments();

    long getTotalActiveAssignments();

    long getTotalAdminAssignments();

    long getTotalDeviceAssignments();

    long getAssignedAlertCount();

    long getUnassignedAlertCount();

    List<AlertAssignmentResponseDto> getAssignmentsByDevice(
            String deviceId);
    
    AlertAssignmentResponseDto getAssignmentById(
            Long assignmentId);
    
    List<AlertAssignmentResponseDto> updateDeviceAlerts(
            AssignDeviceAlertsRequestDto request);

    List<AlertAssignmentResponseDto> getAssignmentsByUser(
            Long userId);
    
   
    List<AlertAssignmentResponseDto> assignAlertsToDevices(
            AssignDeviceAlertsRequestDto request);

   
    List<AlertAssignmentResponseDto> bulkAssignAlertsToDevices(
            BulkAssignDeviceAlertsRequestDto request);
    
    DeviceAlertAssignmentPageResponseDto getDeviceAssignmentOverview(
            int page,
            int size,
            String search,
            String sortBy,
            String sortDirection);
    
    String removeAlertFromDevice(
            String deviceId,
            Long alertId);

   
}
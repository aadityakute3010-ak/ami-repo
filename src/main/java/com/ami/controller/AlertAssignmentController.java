package com.ami.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.ami.dto.requests.AssignAdminAlertsRequestDto;
import com.ami.dto.requests.AssignDeviceAlertsRequestDto;
import com.ami.dto.requests.BulkAssignAlertsRequestDto;
import com.ami.dto.requests.BulkAssignDeviceAlertsRequestDto;
import com.ami.dto.responses.AdminAlertAssignmentPageResponseDto;
import com.ami.dto.responses.AlertAssignmentOverviewResponseDto;
import com.ami.dto.responses.AlertAssignmentResponseDto;
import com.ami.dto.responses.AlertRuleAssignmentPageResponseDto;
import com.ami.dto.responses.AlertRuleAssignmentResponseDto;
import com.ami.dto.responses.DeviceAlertAssignmentPageResponseDto;
import com.ami.service.AlertAssignmentService;

@RestController
@RequestMapping("/api/alert-assignments")
public class AlertAssignmentController {

    private final AlertAssignmentService alertAssignmentService;

    public AlertAssignmentController(
            AlertAssignmentService alertAssignmentService) {

        this.alertAssignmentService =
                alertAssignmentService;
    }

    // =========================================================
    // ADMIN ASSIGNMENT
    // =========================================================

    /**
     * Assign one alert to one or more admins.
     *
     * POST /api/alert-assignments/admins
     */
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @PostMapping("/admins")
    public ResponseEntity<List<AlertAssignmentResponseDto>>
    assignAlertsToAdmins(
            @RequestBody
            AssignAdminAlertsRequestDto request) {

        return ResponseEntity.ok(
                alertAssignmentService
                        .assignAlertsToAdmins(request));
    }

    // =========================================================
    // BULK ADMIN ASSIGNMENT
    // =========================================================

    /**
     * Assign multiple alerts to multiple admins.
     *
     * POST /api/alert-assignments/admins/bulk
     */
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @PostMapping("/admins/bulk")
    public ResponseEntity<List<AlertAssignmentResponseDto>>
    bulkAssignAlertsToAdmins(
            @RequestBody
            BulkAssignAlertsRequestDto request) {

        return ResponseEntity.ok(
                alertAssignmentService
                        .bulkAssignAlertsToAdmins(request));
    }

    // =========================================================
    // GET ADMIN ASSIGNMENTS
    // =========================================================

    /**
     * Get active assignments for an admin.
     *
     * GET /api/alert-assignments/admins/{adminId}
     */
    @GetMapping("/admins/{adminId}")
    public ResponseEntity<List<AlertAssignmentResponseDto>>
    getAssignmentsByAdmin(
            @PathVariable Long adminId) {

        return ResponseEntity.ok(
                alertAssignmentService
                        .getAssignmentsByAdmin(adminId));
    }

    // =========================================================
    // GET ALERT ASSIGNMENTS
    // =========================================================

    /**
     * Get active assignments for an alert.
     *
     * GET /api/alert-assignments/alerts/{alertId}
     */
    @GetMapping("/alerts/{alertId}")
    public ResponseEntity<List<AlertAssignmentResponseDto>>
    getAssignmentsByAlert(
            @PathVariable Long alertId) {

        return ResponseEntity.ok(
                alertAssignmentService
                        .getAssignmentsByAlert(alertId));
    }

    // =========================================================
    // REMOVE ADMIN FROM ALERT
    // =========================================================

    /**
     * Remove an admin assignment from an alert.
     *
     * DELETE /api/alert-assignments/admins/{alertId}/{adminId}
     */
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @DeleteMapping("/admins/{alertId}/{adminId}")
    public ResponseEntity<String>
    removeAdminFromAlert(
            @PathVariable Long alertId,
            @PathVariable Long adminId) {

        return ResponseEntity.ok(
                alertAssignmentService
                        .removeAdminFromAlert(
                                alertId,
                                adminId));
    }

    // =========================================================
    // ENABLE ASSIGNMENT
    // =========================================================

    /**
     * Enable an assignment.
     *
     * PATCH /api/alert-assignments/{assignmentId}/enable
     */
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @PatchMapping("/{assignmentId}/enable")
    public ResponseEntity<AlertAssignmentResponseDto>
    enableAssignment(
            @PathVariable Long assignmentId) {

        return ResponseEntity.ok(
                alertAssignmentService
                        .enableAssignment(
                                assignmentId));
    }

    // =========================================================
    // DISABLE ASSIGNMENT
    // =========================================================

    /**
     * Disable an assignment.
     *
     * PATCH /api/alert-assignments/{assignmentId}/disable
     */
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @PatchMapping("/{assignmentId}/disable")
    public ResponseEntity<AlertAssignmentResponseDto>
    disableAssignment(
            @PathVariable Long assignmentId) {

        return ResponseEntity.ok(
                alertAssignmentService
                        .disableAssignment(
                                assignmentId));
    }

    // =========================================================
    // CHECK ASSIGNMENT
    // =========================================================

    /**
     * Check whether an alert is assigned to an admin.
     *
     * GET /api/alert-assignments/check
     */
    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>>
    checkAssignment(

            @RequestParam Long alertId,

            @RequestParam Long adminId) {

        boolean assigned =
                alertAssignmentService
                        .isAlertAssignedToAdmin(
                                alertId,
                                adminId);

        return ResponseEntity.ok(
                Map.of(
                        "alertId", alertId,
                        "adminId", adminId,
                        "assigned", assigned));
    }

    // =========================================================
    // ASSIGNMENT OVERVIEW
    // =========================================================

    /**
     * Get assignment overview.
     *
     * GET /api/alert-assignments/overview
     */
    @GetMapping("/overview")
    public ResponseEntity<AdminAlertAssignmentPageResponseDto>
    getAssignmentOverview(

            @RequestParam(
                    defaultValue = "0")
            int page,

            @RequestParam(
                    defaultValue = "10")
            int size,

            @RequestParam(
                    required = false)
            String search,

            @RequestParam(
                    defaultValue = "assignedOn")
            String sortBy,
            
            @RequestParam(
                    defaultValue = "DESC")
            String sortDirection) {

        return ResponseEntity.ok(
                alertAssignmentService
                        .getAssignmentOverview(
                                page,
                                size,
                                search,
                                sortBy,
                                sortDirection));
    }
 // =========================================================
 // ALERT RULE ASSIGNMENT
 // =========================================================

 /**
  * Get complete assignment details for one alert rule.
  *
  * GET /api/alert-assignments/alert-rules/{alertId}
  */
 @GetMapping("/alert-rules/{alertId}")
 public ResponseEntity<AlertRuleAssignmentResponseDto>
 getAlertRuleAssignment(
         @PathVariable Long alertId) {

     return ResponseEntity.ok(
             alertAssignmentService
                     .getAlertRuleAssignment(alertId));
 }
 @GetMapping("/alert-rules")
 public ResponseEntity<AlertRuleAssignmentPageResponseDto>
 getAlertRuleAssignments(

         @RequestParam(
                 defaultValue = "0")
         int page,

         @RequestParam(
                 defaultValue = "10")
         int size,

         @RequestParam(
                 required = false)
         String search,

         @RequestParam(
                 defaultValue = "assignedOn")
         String sortBy,

         @RequestParam(
                 defaultValue = "DESC")
         String sortDirection) {

     return ResponseEntity.ok(
             alertAssignmentService
                     .getAlertRuleAssignments(
                             page,
                             size,
                             search,
                             sortBy,
                             sortDirection));
 }
//=========================================================
//DEVICE ASSIGNMENT
//=========================================================

/**
* Assign one alert to one or more devices.
*
* POST /api/alert-assignments/devices
*/
@PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
@PostMapping("/devices")
public ResponseEntity<List<AlertAssignmentResponseDto>>
assignAlertsToDevices(
      @RequestBody
      AssignDeviceAlertsRequestDto request) {

  return ResponseEntity.ok(
          alertAssignmentService
                  .assignAlertsToDevices(request));
}
/**
 * Assign multiple alerts to multiple devices.
 *
 * POST /api/alert-assignments/devices/bulk
 */
@PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
@PostMapping("/devices/bulk")
public ResponseEntity<List<AlertAssignmentResponseDto>>
bulkAssignAlertsToDevices(
        @RequestBody
        BulkAssignDeviceAlertsRequestDto request) {

    return ResponseEntity.ok(
            alertAssignmentService
                    .bulkAssignAlertsToDevices(request));
}
@GetMapping("/devices/overview")
public ResponseEntity<DeviceAlertAssignmentPageResponseDto>
getDeviceAssignmentOverview(

        @RequestParam(defaultValue = "0")
        int page,

        @RequestParam(defaultValue = "10")
        int size,

        @RequestParam(required = false)
        String search,

        @RequestParam(defaultValue = "assignedOn")
        String sortBy,

        @RequestParam(defaultValue = "DESC")
        String sortDirection) {

    return ResponseEntity.ok(
            alertAssignmentService
                    .getDeviceAssignmentOverview(
                            page,
                            size,
                            search,
                            sortBy,
                            sortDirection));
}
//=========================================================
//COMPLETE ASSIGNMENT OVERVIEW
//=========================================================

/**
* Get complete alert assignment overview.
*
* Includes:
* - Admin assignments
* - Device assignments
* - Alert rule assignments
*
* GET /api/alert-assignments/overview/all
*/
@GetMapping("/overview/all")
public ResponseEntity<AlertAssignmentOverviewResponseDto>
getCompleteAssignmentOverview(

     @RequestParam(defaultValue = "0")
     int page,

     @RequestParam(defaultValue = "10")
     int size,

     @RequestParam(required = false)
     String search,

     @RequestParam(defaultValue = "assignedOn")
     String sortBy,

     @RequestParam(defaultValue = "DESC")
     String sortDirection) {

 return ResponseEntity.ok(
         alertAssignmentService
                 .getCompleteAssignmentOverview(
                         page,
                         size,
                         search,
                         sortBy,
                         sortDirection));
}
@PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
@DeleteMapping( "/device-alert-assignments/device/{deviceId}/alert/{alertId}")
public ResponseEntity<String> removeAlertFromDevice(
        @PathVariable String deviceId,
        @PathVariable Long alertId) {

    return ResponseEntity.ok(
            alertAssignmentService
                    .removeAlertFromDevice(
                            deviceId,
                            alertId));
}
//=========================================================
//DEVICE ASSIGNMENT STATUS
//=========================================================

@PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
@PatchMapping("/device/{assignmentId}/enable")
public ResponseEntity<AlertAssignmentResponseDto>
enableDeviceAssignment(
     @PathVariable Long assignmentId) {

 return ResponseEntity.ok(
         alertAssignmentService
                 .enableAssignment(assignmentId));
}

@PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
@PatchMapping("/device/{assignmentId}/disable")
public ResponseEntity<AlertAssignmentResponseDto>
disableDeviceAssignment(
     @PathVariable Long assignmentId) {

 return ResponseEntity.ok(
         alertAssignmentService
                 .disableAssignment(assignmentId));
}
//=========================================================
//ASSIGNMENT HISTORY
//=========================================================

@GetMapping("/alerts/{alertId}/history")
public ResponseEntity<List<AlertAssignmentResponseDto>>
getAssignmentHistory(
     @PathVariable Long alertId) {

 return ResponseEntity.ok(
         alertAssignmentService
                 .getAssignmentHistory(alertId));
}
@PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
@PostMapping("/device-alert-assignments")
public ResponseEntity<List<AlertAssignmentResponseDto>>
assignDeviceAlerts(
        @RequestBody AssignDeviceAlertsRequestDto request) {

    return ResponseEntity.ok(
            alertAssignmentService
                    .assignAlertsToDevices(request));
}
@GetMapping("/device-alert-assignments/device/{deviceId}")
public ResponseEntity<List<AlertAssignmentResponseDto>>
getDeviceAssignments(
        @PathVariable String deviceId) {

    return ResponseEntity.ok(
            alertAssignmentService
                    .getAssignmentsByDevice(deviceId));
}
@GetMapping("/device-alert-assignments/alert/{alertId}")
public ResponseEntity<List<AlertAssignmentResponseDto>>
getDeviceAssignmentsByAlert(
        @PathVariable Long alertId) {

    return ResponseEntity.ok(
            alertAssignmentService
                    .getAssignmentsByAlert(alertId)
                    .stream()
                    .filter(a ->
                            "DEVICE".equalsIgnoreCase(
                                    a.getAssignmentType()))
                    .toList());
}
@PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
@PatchMapping("/device-alert-assignments/{assignmentId}/enable")
public ResponseEntity<AlertAssignmentResponseDto>
enableDeviceAlertAssignment(
        @PathVariable Long assignmentId) {

    return ResponseEntity.ok(
            alertAssignmentService
                    .enableAssignment(assignmentId));
}
@PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
@PatchMapping("/device-alert-assignments/{assignmentId}/disable")
public ResponseEntity<AlertAssignmentResponseDto>
disableDeviceAlertAssignment(
        @PathVariable Long assignmentId) {

    return ResponseEntity.ok(
            alertAssignmentService
                    .disableAssignment(assignmentId));
}
@PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
@DeleteMapping("/device-alert-assignments/{assignmentId}")
public ResponseEntity<String>
deleteDeviceAlertAssignment(
        @PathVariable Long assignmentId) {

    return ResponseEntity.ok(
            alertAssignmentService
                    .unassignAlert(assignmentId));
}
@GetMapping("/device-alert-assignments/{assignmentId}")
public ResponseEntity<AlertAssignmentResponseDto> getDeviceAssignment(
        @PathVariable Long assignmentId) {

    return ResponseEntity.ok(
            alertAssignmentService
                    .getAssignmentById(assignmentId));
}
@GetMapping("/device-alert-assignments/user/{userId}")
public ResponseEntity<List<AlertAssignmentResponseDto>> getUserDeviceAssignments(
        @PathVariable Long userId) {

    return ResponseEntity.ok(
            alertAssignmentService
                    .getAssignmentsByUser(userId));
}
@PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
@PutMapping("/device-alert-assignments")
public ResponseEntity<List<AlertAssignmentResponseDto>>
updateDeviceAlerts(
        @RequestBody AssignDeviceAlertsRequestDto request) {

    return ResponseEntity.ok(
            alertAssignmentService
                    .updateDeviceAlerts(request));
}
}
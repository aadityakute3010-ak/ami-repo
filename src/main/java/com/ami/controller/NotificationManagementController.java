package com.ami.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ami.dto.requests.CreateNotificationRequestDto;
import com.ami.dto.responses.NotificationDashboardResponseDto;
import com.ami.dto.responses.NotificationResponseDto;
import com.ami.enums.NotificationStatus;
import com.ami.enums.NotificationType;
import com.ami.service.NotificationManagementService;

@RestController
@RequestMapping("/api/notifications")
public class NotificationManagementController {

    private final NotificationManagementService service;

    public NotificationManagementController(
            NotificationManagementService service) {

        this.service = service;
    }

    @PostMapping
    public ResponseEntity<NotificationResponseDto>
    createNotification(
            @RequestBody
            CreateNotificationRequestDto request) {

        return ResponseEntity.ok(
                service.createNotification(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponseDto>
    getNotificationById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                service.getNotificationById(id));
    }

    @GetMapping
    public ResponseEntity<Page<NotificationResponseDto>>
    getAllNotifications(

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam(required = false)
            String search,

            @RequestParam(required = false)
            NotificationType type,

            @RequestParam(required = false)
            NotificationStatus status,

            @RequestParam(required = false)
            String recipient,

            @RequestParam(defaultValue = "createdAt")
            String sortBy,

            @RequestParam(defaultValue = "DESC")
            String direction) {

        return ResponseEntity.ok(

                service.getAllNotifications(

                        page,

                        size,

                        search,

                        type,

                        status,

                        recipient,

                        sortBy,

                        direction));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<String>
    markAsRead(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                service.markAsRead(id));
    }

    @PatchMapping("/read-all")
    public ResponseEntity<String>
    markAllAsRead(
            @RequestParam String recipient) {

        return ResponseEntity.ok(
                service.markAllAsRead(recipient));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String>
    deleteNotification(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                service.deleteNotification(id));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<NotificationDashboardResponseDto>
    getDashboard() {

        return ResponseEntity.ok(
                service.getDashboard());
    }
}
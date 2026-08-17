package com.ami.service;

import org.springframework.data.domain.Page;

import com.ami.dto.requests.CreateNotificationRequestDto;
import com.ami.dto.responses.NotificationDashboardResponseDto;
import com.ami.dto.responses.NotificationResponseDto;
import com.ami.enums.NotificationStatus;
import com.ami.enums.NotificationType;

public interface NotificationManagementService {

    NotificationResponseDto createNotification(
            CreateNotificationRequestDto request);

    NotificationResponseDto getNotificationById(
            Long id);

    Page<NotificationResponseDto> getAllNotifications(

            int page,

            int size,

            String search,

            NotificationType type,

            NotificationStatus status,

            String recipient,

            String sortBy,

            String direction);

    String markAsRead(
            Long id);

    String markAllAsRead(
            String recipient);

    String deleteNotification(
            Long id);

    NotificationDashboardResponseDto getDashboard();
}
package com.ami.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.ami.dto.requests.CreateNotificationRequestDto;
import com.ami.dto.responses.NotificationDashboardResponseDto;
import com.ami.dto.responses.NotificationResponseDto;
import com.ami.entity.Notification;
import com.ami.enums.NotificationStatus;
import com.ami.enums.NotificationType;
import com.ami.repository.NotificationRepository;
import com.ami.service.NotificationManagementService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationManagementServiceImpl
        implements NotificationManagementService {

    private final NotificationRepository
            notificationRepository;
    
    @Override
    public NotificationResponseDto
    createNotification(
            CreateNotificationRequestDto request) {

        Notification notification =

                Notification.builder()

                        .type(
                                request.getType())

                        .status(
                                NotificationStatus.UNREAD)

                        .title(
                                request.getTitle())

                        .message(
                                request.getMessage())

                        .recipient(
                                request.getRecipient())

                        .createdAt(
                                LocalDateTime.now())

                        .build();

        notification =

                notificationRepository.save(
                        notification);

        return mapToResponse(
                notification);
    }
    @Override
    public NotificationResponseDto
    getNotificationById(
            Long id) {

        Notification notification =

                notificationRepository

                        .findById(id)

                        .orElseThrow(() ->

                                new RuntimeException(
                                        "Notification not found"));

        return mapToResponse(
                notification);
    }
    @Override
    public Page<NotificationResponseDto>
    getAllNotifications(

            int page,

            int size,

            String search,

            NotificationType type,

            NotificationStatus status,

            String recipient,

            String sortBy,

            String direction) {

        Sort sort = direction.equalsIgnoreCase("DESC")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        sort);

        Specification<Notification> specification =
                (root, query, cb) -> cb.conjunction();

        if (search != null && !search.isBlank()) {

            specification = specification.and((root, query, cb) ->

                    cb.or(

                            cb.like(
                                    cb.lower(root.get("title")),
                                    "%" + search.toLowerCase() + "%"),

                            cb.like(
                                    cb.lower(root.get("message")),
                                    "%" + search.toLowerCase() + "%"),

                            cb.like(
                                    cb.lower(root.get("recipient")),
                                    "%" + search.toLowerCase() + "%")));
        }

        if (type != null) {

            specification = specification.and((root, query, cb) ->

                    cb.equal(root.get("type"), type));
        }

        if (status != null) {

            specification = specification.and((root, query, cb) ->

                    cb.equal(root.get("status"), status));
        }

        if (recipient != null && !recipient.isBlank()) {

            specification = specification.and((root, query, cb) ->

                    cb.equal(root.get("recipient"), recipient));
        }

        return notificationRepository

                .findAll(
                        specification,
                        pageable)

                .map(this::mapToResponse);
    }
    
    @Override
    public String markAsRead(
            Long id) {

        Notification notification =

                notificationRepository

                        .findById(id)

                        .orElseThrow(() ->

                                new RuntimeException(
                                        "Notification not found"));

        notification.setStatus(
                NotificationStatus.READ);

        notification.setReadAt(
                LocalDateTime.now());

        notificationRepository.save(
                notification);

        return "Notification marked as read";
    }
    @Override
    public String markAllAsRead(
            String recipient) {

        List<Notification> notifications =

                notificationRepository

                        .findByRecipient(
                                recipient);

        notifications.forEach(notification -> {

            notification.setStatus(
                    NotificationStatus.READ);

            notification.setReadAt(
                    LocalDateTime.now());
        });

        notificationRepository.saveAll(
                notifications);

        return "All notifications marked as read";
    }
    @Override
    public String deleteNotification(
            Long id) {

        Notification notification =

                notificationRepository

                        .findById(id)

                        .orElseThrow(() ->

                                new RuntimeException(
                                        "Notification not found"));

        notificationRepository.delete(
                notification);

        return "Notification deleted successfully";
    }
    @Override
    public NotificationDashboardResponseDto
    getDashboard() {

        List<Notification> list =
                notificationRepository.findAll();

        return NotificationDashboardResponseDto

                .builder()

                .totalNotifications(
                        (long) list.size())

                .unreadNotifications(
                        list.stream()
                                .filter(n ->
                                        n.getStatus() == NotificationStatus.UNREAD)
                                .count())

                .readNotifications(
                        list.stream()
                                .filter(n ->
                                        n.getStatus() == NotificationStatus.READ)
                                .count())

                .systemNotifications(
                        list.stream()
                                .filter(n ->
                                        n.getType() == NotificationType.SYSTEM)
                                .count())

                .deviceNotifications(
                        list.stream()
                                .filter(n ->
                                        n.getType() == NotificationType.DEVICE)
                                .count())

                .alertNotifications(
                        list.stream()
                                .filter(n ->
                                        n.getType() == NotificationType.ALERT)
                                .count())

                .maintenanceNotifications(
                        list.stream()
                                .filter(n ->
                                        n.getType() == NotificationType.MAINTENANCE)
                                .count())

                .firmwareNotifications(
                        list.stream()
                                .filter(n ->
                                        n.getType() == NotificationType.FIRMWARE)
                                .count())

                .administrationNotifications(
                        list.stream()
                                .filter(n ->
                                        n.getType() == NotificationType.ADMINISTRATION)
                                .count())

                .build();
    }
    private NotificationResponseDto
    mapToResponse(
            Notification notification) {

        return NotificationResponseDto

                .builder()

                .id(
                        notification.getId())

                .type(
                        notification.getType())

                .status(
                        notification.getStatus())

                .title(
                        notification.getTitle())

                .message(
                        notification.getMessage())

                .recipient(
                        notification.getRecipient())

                .createdAt(
                        notification.getCreatedAt())

                .readAt(
                        notification.getReadAt())

                .build();
    }

}
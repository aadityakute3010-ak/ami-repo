package com.ami.service.impl;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.ami.dto.requests.CreateNotificationRequestDto;
import com.ami.dto.responses.AlertNotificationDto;
import com.ami.enums.NotificationType;
import com.ami.service.NotificationManagementService;
import com.ami.service.NotificationService;

@Service
public class NotificationServiceImpl
        implements NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    private final NotificationManagementService
            notificationManagementService;

    public NotificationServiceImpl(

            SimpMessagingTemplate messagingTemplate,

            NotificationManagementService
                    notificationManagementService) {

        this.messagingTemplate = messagingTemplate;
        this.notificationManagementService =
                notificationManagementService;
    }

    @Override
    public void issueAssigned(
            Long issueId,
            String engineerName) {

        messagingTemplate.convertAndSend(
                "/topic/issues",
                AlertNotificationDto.builder()
                        .alertId(issueId)
                        .alertName("Issue Assigned")
                        .severity("INFO")
                        .message("Issue assigned to " + engineerName)
                        .build());

        notificationManagementService.createNotification(

                CreateNotificationRequestDto.builder()

                        .type(NotificationType.ALERT)

                        .title("Issue Assigned")

                        .message("Issue assigned to " + engineerName)

                        .recipient(engineerName)

                        .build());
    }

    @Override
    public void issueAccepted(
            Long issueId) {

        messagingTemplate.convertAndSend(
                "/topic/issues",
                AlertNotificationDto.builder()
                        .alertId(issueId)
                        .alertName("Issue Accepted")
                        .severity("INFO")
                        .message("Issue accepted by engineer")
                        .build());

        notificationManagementService.createNotification(

                CreateNotificationRequestDto.builder()

                        .type(NotificationType.ALERT)

                        .title("Issue Accepted")

                        .message("Issue accepted by engineer")

                        .recipient("ADMIN")

                        .build());
    }

    @Override
    public void issueRejected(
            Long issueId,
            String reason) {

        messagingTemplate.convertAndSend(
                "/topic/issues",
                AlertNotificationDto.builder()
                        .alertId(issueId)
                        .alertName("Issue Rejected")
                        .severity("WARNING")
                        .message(reason)
                        .build());

        notificationManagementService.createNotification(

                CreateNotificationRequestDto.builder()

                        .type(NotificationType.ALERT)

                        .title("Issue Rejected")

                        .message(reason)

                        .recipient("ADMIN")

                        .build());
    }

    @Override
    public void issueEscalated(
            Long issueId) {

        messagingTemplate.convertAndSend(
                "/topic/issues",
                AlertNotificationDto.builder()
                        .alertId(issueId)
                        .alertName("Issue Escalated")
                        .severity("CRITICAL")
                        .message("Issue escalated")
                        .build());

        notificationManagementService.createNotification(

                CreateNotificationRequestDto.builder()

                        .type(NotificationType.ALERT)

                        .title("Issue Escalated")

                        .message("Issue escalated")

                        .recipient("ADMIN")

                        .build());
    }

    @Override
    public void issueResolved(
            Long issueId) {

        messagingTemplate.convertAndSend(
                "/topic/issues",
                AlertNotificationDto.builder()
                        .alertId(issueId)
                        .alertName("Issue Resolved")
                        .severity("SUCCESS")
                        .message("Issue resolved successfully")
                        .build());

        notificationManagementService.createNotification(

                CreateNotificationRequestDto.builder()

                        .type(NotificationType.ALERT)

                        .title("Issue Resolved")

                        .message("Issue resolved successfully")

                        .recipient("ADMIN")

                        .build());
    }

    @Override
    public void slaBreached(
            Long issueId) {

        messagingTemplate.convertAndSend(
                "/topic/issues",
                AlertNotificationDto.builder()
                        .alertId(issueId)
                        .alertName("SLA Breached")
                        .severity("CRITICAL")
                        .message("Issue SLA has been breached")
                        .build());

        notificationManagementService.createNotification(

                CreateNotificationRequestDto.builder()

                        .type(NotificationType.ALERT)

                        .title("SLA Breached")

                        .message("Issue SLA has been breached")

                        .recipient("ADMIN")

                        .build());
    }

    @Override
    public void fieldVisitCreated(
            Long issueId) {

        messagingTemplate.convertAndSend(
                "/topic/issues",
                AlertNotificationDto.builder()
                        .alertId(issueId)
                        .alertName("Field Visit")
                        .severity("INFO")
                        .message("Field visit created")
                        .build());

        notificationManagementService.createNotification(

                CreateNotificationRequestDto.builder()

                        .type(NotificationType.MAINTENANCE)

                        .title("Field Visit")

                        .message("Field visit created")

                        .recipient("ENGINEER")

                        .build());
    }
    @Override
    public void workStarted(
            Long issueId) {

        messagingTemplate.convertAndSend(
                "/topic/issues",
                AlertNotificationDto.builder()
                        .alertId(issueId)
                        .alertName("Work Started")
                        .severity("INFO")
                        .message("Engineer started working")
                        .build());

        notificationManagementService.createNotification(

                CreateNotificationRequestDto.builder()

                        .type(NotificationType.MAINTENANCE)

                        .title("Work Started")

                        .message("Engineer started working")

                        .recipient("ADMIN")

                        .build());
    }
    @Override
    public void materialAdded(
            Long issueId,
            String materialName) {

        messagingTemplate.convertAndSend(
                "/topic/issues",
                AlertNotificationDto.builder()
                        .alertId(issueId)
                        .alertName("Material Added")
                        .severity("INFO")
                        .message(materialName + " added")
                        .build());

        notificationManagementService.createNotification(

                CreateNotificationRequestDto.builder()

                        .type(NotificationType.MAINTENANCE)

                        .title("Material Added")

                        .message(materialName + " added")

                        .recipient("ENGINEER")

                        .build());
    }
    
    @Override
    public void alertCreated(
            Long alertId,
            String alertName,
            String severity,
            String message) {

        messagingTemplate.convertAndSend(

                "/topic/alerts",

                AlertNotificationDto.builder()

                        .alertId(alertId)

                        .alertName(alertName)

                        .severity(severity)

                        .message(message)

                        .build());
        
        notificationManagementService.createNotification(

                CreateNotificationRequestDto.builder()

                        .type(
                                NotificationType.ALERT)

                        .title(
                                alertName)

                        .message(
                                message)

                        .recipient(
                                "ADMIN")

                        .build());
    }
    @Override
    public void progressUpdated(
            Long issueId,
            Integer progress) {

        messagingTemplate.convertAndSend(
                "/topic/issues",
                AlertNotificationDto.builder()
                        .alertId(issueId)
                        .alertName("Progress Updated")
                        .severity("INFO")
                        .message("Progress updated to " + progress + "%")
                        .build());

        notificationManagementService.createNotification(

                CreateNotificationRequestDto.builder()

                        .type(NotificationType.ALERT)

                        .title("Progress Updated")

                        .message("Progress updated to " + progress + "%")

                        .recipient("ADMIN")

                        .build());
    }
    @Override
    public void issueClosed(
            Long issueId) {

        messagingTemplate.convertAndSend(
                "/topic/issues",
                AlertNotificationDto.builder()
                        .alertId(issueId)
                        .alertName("Issue Closed")
                        .severity("SUCCESS")
                        .message("Issue closed successfully")
                        .build());

        notificationManagementService.createNotification(

                CreateNotificationRequestDto.builder()

                        .type(NotificationType.ALERT)

                        .title("Issue Closed")

                        .message("Issue closed successfully")

                        .recipient("ADMIN")

                        .build());
    }
}
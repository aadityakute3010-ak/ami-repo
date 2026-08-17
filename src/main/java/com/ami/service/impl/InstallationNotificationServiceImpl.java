package com.ami.service.impl;

import java.time.LocalDateTime;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.ami.dto.responses.InstallationNotificationDto;
import com.ami.service.InstallationNotificationService;

@Service
public class InstallationNotificationServiceImpl
        implements InstallationNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public InstallationNotificationServiceImpl(
            SimpMessagingTemplate messagingTemplate) {

        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void sendInstallationNotification(
            Long installationId,
            String installationNumber,
            String title,
            String message,
            String event,
            String performedBy) {

        InstallationNotificationDto dto =
                InstallationNotificationDto.builder()
                        .installationId(installationId)
                        .installationNumber(installationNumber)
                        .title(title)
                        .message(message)
                        .event(event)
                        .performedBy(performedBy)
                        .timestamp(LocalDateTime.now())
                        .build();

        messagingTemplate.convertAndSend(
                "/topic/installations",
                dto);
    }
}
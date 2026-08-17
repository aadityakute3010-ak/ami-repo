package com.ami.controller;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import com.ami.dto.responses.AlertNotificationDto;

@RestController
@RequestMapping("/api/ws")
public class WebSocketTestController {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketTestController(
            SimpMessagingTemplate messagingTemplate) {

        this.messagingTemplate = messagingTemplate;
    }

    @PostMapping("/notify")
    public String sendNotification() {

        AlertNotificationDto dto =
                AlertNotificationDto.builder()
                        .alertId(999L)
                        .alertName("Test Alert")
                        .severity("CRITICAL")
                        .message("WebSocket Test")
                        .build();

        messagingTemplate.convertAndSend(
                "/topic/alerts",
                dto);

        return "Notification sent";
    }
}
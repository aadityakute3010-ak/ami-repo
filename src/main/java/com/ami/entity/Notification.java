package com.ami.entity;

import java.time.LocalDateTime;

import com.ami.enums.NotificationStatus;
import com.ami.enums.NotificationType;

import jakarta.persistence.*;

import lombok.*;

@Entity
@Table(
        name = "notifications",
        indexes = {

                @Index(
                        name = "idx_notification_type",
                        columnList = "type"),

                @Index(
                        name = "idx_notification_recipient",
                        columnList = "recipient"),

                @Index(
                	    name = "idx_notification_read",
                	    columnList = "readAt"
                	),

                @Index(
                        name = "idx_notification_created",
                        columnList = "createdAt")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    private String title;

    @Column(length = 4000)
    private String message;

    private String recipient;

    private LocalDateTime createdAt;

    private LocalDateTime readAt;
}
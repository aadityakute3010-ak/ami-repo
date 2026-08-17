package com.ami.entity;

import java.time.LocalDateTime;

import com.ami.enums.SourceType;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "device_operations",
        indexes = {

                @Index(
                        name = "idx_operation_device",
                        columnList = "deviceId"),

                @Index(
                        name = "idx_operation_source",
                        columnList = "sourceType"),

                @Index(
                        name = "idx_operation_status",
                        columnList = "status"),

                @Index(
                        name = "idx_operation_type",
                        columnList = "operationType"),

                @Index(
                        name = "idx_operation_requested",
                        columnList = "requestedAt")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceOperation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String deviceId;

    @Enumerated(EnumType.STRING)
    private SourceType sourceType;

    private String operationType;

    private String title;

    @Column(length = 5000)
    private String description;

    private String severity;

    private String status;

    private String assignedTo;

    private String rootCause;

    private Double latitude;

    private Double longitude;

    private Boolean resolved;
    
    private String responseMessage;
    
    private String requestedBy;

    private LocalDateTime requestedAt;

    private LocalDateTime completedAt;
    
    @Column(length = 3000)
    private String response;

    @Column(length = 2000)
    private String remarks;

    private java.time.LocalDateTime executedAt;
    
    private String acknowledgedBy;

    private LocalDateTime acknowledgedAt;
}
package com.ami.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "firmware_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FirmwareHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String deviceId;

    private String firmwareVersion;

    private String previousVersion;

    private String updateStatus;

    private String updatedBy;

    private LocalDateTime updatedAt;

    private String remarks;
}
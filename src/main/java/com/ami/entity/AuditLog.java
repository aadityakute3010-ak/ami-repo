package com.ami.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "audit_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String module;

    private Long entityId;
    
    @Column(name = "entity_type", length = 50)
    private String entityType;
    
    @Column(name = "target_admin_id")
    private Long targetAdminId;

    private String action;

    private String performedBy;

    private String description;

    private LocalDateTime timestamp;
}
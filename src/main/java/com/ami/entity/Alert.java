

package com.ami.entity;

import java.time.LocalDateTime;

import com.ami.enums.AlertCategory;
import com.ami.enums.AlertSeverity;
import com.ami.enums.AlertSource;
import com.ami.enums.AlertStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Index;
@Entity
@Table(
        name = "alerts",
        indexes = {

                @Index(
                        name = "idx_alert_name",
                        columnList = "name"),

                @Index(
                        name = "idx_alert_category",
                        columnList = "category"),

                @Index(
                        name = "idx_alert_severity",
                        columnList = "severity"),

                @Index(
                        name = "idx_alert_status",
                        columnList = "status"),

                @Index(
                	    name = "idx_alert_source_type",
                	    columnList = "source"
                	),

                @Index(
                        name = "idx_alert_created_at",
                        columnList = "createdAt"),
                
                @Index(
                        name = "idx_alert_archived",
                        columnList = "archived")
        }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String fieldLabel;

    private String placeholder;

    private Boolean enabled;

    private String value;
    
    private String deviceId;

    @Column(length = 1000)
    private String message;
    
    private LocalDateTime resolvedAt;

    @Enumerated(EnumType.STRING)
    private AlertSeverity severity;

    @Enumerated(EnumType.STRING)
    private AlertSource source;

    @Enumerated(EnumType.STRING)
    private AlertCategory category;

    private String description;

    private String unit;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
    
    private LocalDateTime acknowledgedAt;

    private String acknowledgedBy;

    private String resolvedBy;
    
    @Enumerated(EnumType.STRING)
    private AlertStatus status;
    
    @Builder.Default
    private Boolean archived = false;

    private LocalDateTime archivedAt;

    private String archivedBy;
}
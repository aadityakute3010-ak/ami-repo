

package com.ami.entity;

import java.time.LocalDateTime;

import com.ami.enums.AlertCategory;
import com.ami.enums.AlertSeverity;
import com.ami.enums.AlertSource;
import com.ami.enums.AlertStatus;

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

@Entity
@Table(name = "alerts")
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
    
    @Enumerated(EnumType.STRING)
    private AlertStatus status;
}
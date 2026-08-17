package com.ami.entity;

import java.time.LocalDateTime;

import com.ami.enums.ConfigurationStatus;
import com.ami.enums.ConfigurationType;

import jakarta.persistence.*;

import lombok.*;

@Entity
@Table(name = "administration_configuration")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdministrationConfiguration extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ConfigurationType configurationType;

    @Enumerated(EnumType.STRING)
    private ConfigurationStatus status;

    private String configurationName;

    @Column(length = 4000)
    private String configurationValue;

    private String updatedBy;

    private LocalDateTime updatedAt;

    private String remarks;
}
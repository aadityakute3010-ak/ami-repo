package com.ami.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import lombok.*;

@Entity
@Table(name = "configuration_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfigurationHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long configurationId;

    private String configurationName;

    @Column(length = 4000)
    private String oldValue;

    @Column(length = 4000)
    private String newValue;

    private String updatedBy;

    private LocalDateTime updatedAt;
}
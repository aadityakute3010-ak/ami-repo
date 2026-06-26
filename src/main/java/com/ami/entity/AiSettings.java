package com.ami.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ai_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiSettings extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String provider;

    private String model;

    private Double temperature;

    private Integer maxTokens;

    private Boolean enabled;
}
package com.ami.entity;

import java.time.LocalDateTime;

import com.ami.enums.SourceType;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tariffs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tariff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tariffName;

    @Enumerated(EnumType.STRING)
    private SourceType source;

    private Double ratePerUnit;

    private Double fixedCharge;

    private Double taxPercentage;

    private String description;

    private Boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
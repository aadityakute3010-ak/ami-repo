package com.ami.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tariff_slabs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TariffSlab {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long tariffId;

    private Double fromUnit;

    private Double toUnit;

    private Double ratePerUnit;
}
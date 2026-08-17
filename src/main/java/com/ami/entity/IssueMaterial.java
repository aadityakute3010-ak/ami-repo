package com.ami.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "issue_materials")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IssueMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issue_id", nullable = false)
    private Issue issue;

    @Column(nullable = false)
    private String materialName;

    @Column(nullable = false)
    private Double quantity;

    private String unit;

    private Double cost;

    @Column(length = 1000)
    private String remarks;
}
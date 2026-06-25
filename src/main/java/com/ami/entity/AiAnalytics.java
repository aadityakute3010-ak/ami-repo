package com.ami.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ai_analytics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiAnalytics extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    @Column(columnDefinition = "TEXT")
    private String query;

    private String module;

    private Long responseTime;

    private String feedback;
}
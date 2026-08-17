package com.ami.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "field_visits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FieldVisit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issue_id", nullable = false)
    private Issue issue;

    private Long engineerId;

    private String engineerName;

    private LocalDateTime visitDate;

    private LocalDateTime checkIn;

    private LocalDateTime checkOut;

    private Double latitude;

    private Double longitude;

    @Column(length = 3000)
    private String observation;

    private String photoUrl;

    private String status;
}
package com.ami.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.ami.enums.LeaveStatus;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "engineer_leaves")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EngineerLeave {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long engineerId;

    private LocalDate fromDate;

    private LocalDate toDate;

    @Column(length = 1000)
    private String reason;

    @Enumerated(EnumType.STRING)
    private LeaveStatus status;

    private LocalDateTime appliedAt;

    private LocalDateTime actionAt;
}
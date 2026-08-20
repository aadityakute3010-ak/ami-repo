package com.ami.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "issue_progress")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IssueProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long issueId;

    private String progress;

    @Column(length = 1000)
    private String remarks;

    private LocalDateTime createdAt;
}
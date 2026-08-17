package com.ami.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "issue_rejection_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IssueRejectionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issue_id", nullable = false)
    private Issue issue;

    private Long engineerId;

    private String engineerName;

    @Column(length = 3000)
    private String reason;

    @Column(length = 3000)
    private String comment;

    @Builder.Default
    private LocalDateTime rejectedAt = LocalDateTime.now();
}
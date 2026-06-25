package com.ami.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "blacklisted_tokens")
public class BlacklistedToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 1000)
    private String token;

    private LocalDateTime blacklistedAt;

    public BlacklistedToken() {
    }

    public BlacklistedToken(String token,LocalDateTime blacklistedAt) {
        this.token = token;
        this.blacklistedAt = blacklistedAt;
    } 

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getBlacklistedAt() {
        return blacklistedAt;
    }

    public void setBlacklistedAt(
            LocalDateTime blacklistedAt
    ) {
        this.blacklistedAt = blacklistedAt;
    }
}